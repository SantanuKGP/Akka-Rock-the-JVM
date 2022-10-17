package actors

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object ActorsIntro3 {

  def demoSimpleActor(str : String):Unit={
    // Step 2 : instantiate
    val actorSystem = ActorSystem(simpleActor(),"FirstActorSystem")

    // Step 3 : communicate
    actorSystem ! str // asynchronously sends message
    actorSystem ! "***************"
    // Step 4 : Shut Down [ Not recommended]
    actorSystem.terminate()
  }
  object simpleActor{
    def apply(): Behavior[String] = Behaviors.setup{context =>
      // actor "private" data and methods, behaviours etc
      // Your code here


      // behavior used for the first message
      Behaviors.receiveMessage{(message: String) =>
        context.log.info(s"I received: $message")
        Behaviors.same
      }
    }
  }



  def main(args: Array[String]): Unit = {
    demoSimpleActor("I love Akka!")
    demoSimpleActor("I love Scala!")
  }
}
