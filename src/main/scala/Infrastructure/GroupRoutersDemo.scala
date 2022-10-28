package Infrastructure

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.{Behaviors, Routers}
import utils._

import scala.concurrent.duration._

object GroupRoutersDemo {
  def demo_GroupRouter():Unit={
    val serviceKey = ServiceKey[String]("logWorker")
    val user= Behaviors.setup[Unit]{ context=>
      val workers = (1 to 5).map(i=> context.spawn(LoggerActor[String](), s"worker-$i"))
      workers.foreach(worker => context.system.receptionist ! Receptionist.Register(serviceKey,worker))

      val groupBehavior: Behavior[String] = Routers.group(serviceKey).withRoundRobinRouting()
      val groupRouter = context.spawn(groupBehavior,"WorkerGroup")

      (1 to 10).foreach(i=> groupRouter ! s"work task $i")

      Thread.sleep(100)
      val extraWorker =  context.spawn(LoggerActor[String](),"extraWorker")
      context.system.receptionist ! Receptionist.Register(serviceKey, extraWorker)
      (1 to 10).foreach(i=> groupRouter ! s"work task $i")

      Behaviors.empty
    }
    ActorSystem(user,"DemoPoolRouters").withFiniteLifeSpan(2.seconds)
  }
  def main(args: Array[String]): Unit = {
    demo_GroupRouter()
  }

}
