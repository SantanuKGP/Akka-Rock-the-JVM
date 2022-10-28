package Infrastructure

import akka.actor.Cancellable
import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.duration._

object Schedulers {

  object LoggerActor{
    def apply(): Behavior[String]= Behaviors.receive{ (context,message)=>
      context.log.info(s"[${context.self.path}] received: $message")
      Behaviors.same
    }
  }

  def demoScheduler():Unit={
    val userGuardian= Behaviors.setup[Unit]{ context=>
      val logger_actor = context.spawn(LoggerActor(),"loggerActor")
      context.log.info("System  is starting")
      context.scheduleOnce(1.second,logger_actor,"reminder")
      Behaviors.empty
    }

    val system= ActorSystem(userGuardian,"ActorSystem")
    /*
    Thread.sleep(2000)
    system.terminate()
    */
    import system.executionContext
    system.scheduler.scheduleOnce(2.seconds,()=>system.terminate())
  }

  def demoActorWithTimeout():Unit={
    val timeoutActor: Behavior[String] = Behaviors.receive{ (context, message)=>
      val schedule = context.scheduleOnce(1.second,context.self,"timeout")
//      schedule.cancel()
      message match{
        case "timeout" =>
          context.log.info("Stopping!")
          Behaviors.stopped
        case _ =>
          context.log.info(s"Received: $message")
          Behaviors.same
      }
    }

    val system = ActorSystem(timeoutActor,"TimeOutDemo")
    system ! "Hello World!"
    Thread.sleep(2000)
    system ! "Are you there?"
    system.terminate()
  }

  def resettingTimeoutActor(schedule: Cancellable):Behavior[String] =Behaviors.receive{ (context,message)=>
    message match{
      case "timeout" =>
        context.log.info("Stopping")
        Behaviors.stopped
      case _ =>
        context.log.info(s"Received: $message")
        schedule.cancel()
        resettingTimeoutActor(context.scheduleOnce(1.second,context.self,"timeout"))
    }
  }

  object ResettingTimeoutActor{
    def apply():Behavior[String]= Behaviors.receive{(context,message)=>
      context.log.info(s"Received: $message")
      resettingTimeoutActor(context.scheduleOnce(1.second,context.self,"timeout"))
    }
  }
  def demoActorResettingTimeout(): Unit ={
    val userGuardian= Behaviors.setup[Unit]{ context=>
//      val schedule = context.scheduleOnce(1.second,context.self,"timeout")
      val resetActor = context.spawn(ResettingTimeoutActor(),"resettingTimeoutActor")
      context.log.info("System  is starting")

      resetActor ! "Start timer"
      Thread.sleep(500)
      resetActor ! "this should still be visible"
      Thread.sleep(700)
      resetActor ! "this could still be visible"
      Thread.sleep(1200)
      resetActor ! "this should not be visible"

      Behaviors.empty
    }
    import utils._
    val system= ActorSystem(userGuardian,"ActorSystem").withFiniteLifeSpan(4.second)
    /*
    import system.executionContext
    system.scheduler.scheduleOnce(3.second,()=>system.terminate())
    */
  }
  def main(args: Array[String]): Unit = {
//    demoScheduler()
//    demoActorWithTimeout()
    demoActorResettingTimeout()
  }
}
