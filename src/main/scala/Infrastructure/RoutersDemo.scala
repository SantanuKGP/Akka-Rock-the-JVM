package Infrastructure

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.{Behaviors, Routers}
import utils._

import scala.concurrent.duration._

object RoutersDemo {
  def demoPoolRouter():Unit={
    val workBehavior= LoggerActor[String]()
    val poolRouter =  Routers.pool(5)(workBehavior).withBroadcastPredicate(_.length>11)

    val user = Behaviors.setup[Unit]{ context =>
      val poolActor = context.spawn(poolRouter,"pool")
      (1 to 10).foreach(i => poolActor ! s"work task $i")
      Behaviors.empty
    }
    ActorSystem(user,"DemoPoolRouters").withFiniteLifeSpan(2.seconds)
  }

  def main(args: Array[String]): Unit = {
    demoPoolRouter()
  }
}
