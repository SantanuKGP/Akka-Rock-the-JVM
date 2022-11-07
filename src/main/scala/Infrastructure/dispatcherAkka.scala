package Infrastructure

import akka.actor.typed.{ActorSystem, Behavior, DispatcherSelector}
import akka.actor.typed.scaladsl.Behaviors
import utils._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Random

object dispatcherAkka {
  def demoDispatcherConfig() : Unit={
    val userGuardian = Behaviors.setup[Unit] { context =>
      val childActorDispatcherDefault = context.spawn(LoggerActor[String](), "childActorDispatcherDefault", DispatcherSelector.default())
      val childActorBlocking = context.spawn(LoggerActor[String](), "childActorBlocking", DispatcherSelector.blocking())
      val childActorInherit = context.spawn(LoggerActor[String](), "childActorInherit", DispatcherSelector.sameAsParent())
      val childActorConfig = context.spawn(LoggerActor[String](), "childActorConfig", DispatcherSelector.fromConfig("my-dispatcher"))

      val actors = (1 to 10).map(i => context.spawn(LoggerActor[String](), s"child$i", DispatcherSelector.fromConfig("my-dispatcher")))

      val r = new Random()
      (1 to 1000).foreach(i => actors(r.nextInt(10)) ! s"task$i")
      Behaviors.empty
    }
    ActorSystem(userGuardian,"DemoDispatcher").withFiniteLifeSpan(2.seconds)
  }
  object DBActor{
    def apply():Behavior[String]= Behaviors.receive{(context,message)=>
      import context.executionContext
      Future{
        Thread.sleep(1000)
        println(s"Query successful: $message")
      }
      Behaviors.same
    }
  }
  def demoBlockingCalls():Unit ={
    val userGuardian = Behaviors.setup[Unit]{ context =>
      val loggerActor = context.spawn(LoggerActor[String](),"logger")
      val dbActor = context.spawn(DBActor(),"DB",
//        DispatcherSelector.fromConfig("dispatchers-demo.dedicated-blocking-dispatcher")
                )

      (1 to 100).foreach{i=>
        val message = s"query $i"
        dbActor ! message
        loggerActor ! message
      }

      Behaviors.same
    }
    val system = ActorSystem(userGuardian,"DemoBlockingCalls")
    Thread.sleep(12000)
    system.terminate()
  }
  def main(args: Array[String]): Unit = {
//    demoDispatcherConfig()
    demoBlockingCalls()
  }
}
