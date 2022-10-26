package childActors

import akka.actor.typed.{ActorSystem, Behavior, PostStop}
import akka.actor.typed.scaladsl.Behaviors

object stoppingActor {

  object SensitiveActor{
    def apply():Behavior[String] = Behaviors.receive[String]{(context,message)=>
      context.log.info(s"Received : $message")
      if(message=="you are ugly") {
//        Behaviors.stopped(()=> context.log.info("I am stopped now, not receiving other messages"))
        Behaviors.stopped
      } else Behaviors.same
    }
      .receiveSignal{
        case(context,PostStop)=> context.log.info("I am stopping now!")
          Behaviors.same
      }
  }
  def main(args: Array[String]): Unit = {
    val userGuardian = Behaviors.setup[Unit]{ context=>

      val actor= context.spawn(SensitiveActor(),"Sensitive_Actor")

      actor! "Hi"
      actor! "How are you?"
      actor! "you are ugly"
      actor! "Sorry about that!"

      Behaviors.empty
    }

    val system= ActorSystem(userGuardian,"DemoStoppingActor")
    system.terminate()
  }
}
