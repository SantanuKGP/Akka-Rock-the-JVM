package actors

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object ActorsIntro {

  // Step 1 : create Behavior
  val simpleActorBehaviour: Behavior[String] = Behaviors.receiveMessage{(message: String) =>
    // do something with the message
    println(s"I received: $message")

    // new behavior for the next message
    Behaviors.same
  }

  def demoSimpleActor(str : String):Unit={
    // Step 2 : instantiate
    val actorSystem = ActorSystem(simpleActorBehaviour,"FirstActorSystem")

    // Step 3 : communicate
    actorSystem ! str // asynchronously sends message
    actorSystem ! "***************"
    // Step 4 : Shut Down [ Not recommended]
    actorSystem.terminate()
  }

  def main(args: Array[String]): Unit = {
    demoSimpleActor("I love Akka!")
    demoSimpleActor("I love Scala!")
  }
}
