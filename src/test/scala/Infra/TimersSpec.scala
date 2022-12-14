package Infra

import akka.actor.testkit.typed.scaladsl.ManualTime
import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration._




class TimersSpec extends ScalaTestWithActorTestKit(ManualTime.config) with AnyWordSpecLike{
  import TimersSpec._
  "A reporter" should{
    "trigger report an hour" in{
      val probe = testKit.createTestProbe[Command]()
      val time: ManualTime = ManualTime()

      testKit.spawn(Reporter(probe.ref))
      probe.expectNoMessage(1.second)
      time.timePasses(1.hour)
      probe.expectMessage(Report)
    }
  }
}

object TimersSpec {
  trait Command
  case object Timeout extends Command
  case object Report extends Command

  object Reporter{
    def apply(destination: ActorRef[Command]):Behavior[Command]= Behaviors.withTimers{ timer=>
      timer.startSingleTimer(Timeout,1.hour)

      Behaviors.receiveMessage{
        case Timeout => destination ! Report
          Behaviors.same
      }
    }
  }

}
