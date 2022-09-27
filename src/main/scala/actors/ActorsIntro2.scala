package actors

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object ActorsIntro2 {

  def demoSimpleActor(str : String):Unit={
    // Step 2 : instantiate
    val actorSystem = ActorSystem(simpleActor_v2(),"FirstActorSystem")

    // Step 3 : communicate
    actorSystem ! str // asynchronously sends message
    actorSystem ! "***************"
    // Step 4 : Shut Down [ Not recommended]
    actorSystem.terminate()
  }
  object simpleActor{
    def apply(): Behavior[String] = Behaviors.receiveMessage{(message: String) =>
      // do something with the message
      println(s"I received: $message")

      // new behavior for the next message
      Behaviors.same
    }
  }

  object simpleActor_v2{
    def apply(): Behavior[String] = Behaviors.receive{(context,message) =>
      // context is a data structure (ActorContext) with access to a variety of APIs
      // simple example : logging
      context.log.info(s"I received: $message")

      // new behavior for the next message
      Behaviors.same
    }
  }

  def main(args: Array[String]): Unit = {
    demoSimpleActor("I love Akka!")
    demoSimpleActor("I love Scala!")
  }
}