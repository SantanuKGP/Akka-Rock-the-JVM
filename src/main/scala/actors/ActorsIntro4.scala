package actors

import actors.ActorsIntro3.simpleActor
import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object ActorsIntro4 {
  def demoSimpleActor(str : String):Unit={
    // Step 2 : instantiate
    val actorSystem = ActorSystem(Person(),"FirstActorSystem")

    // Step 3 : communicate
    actorSystem ! str // asynchronously sends message
    actorSystem ! "***************"
    // Step 4 : Shut Down [ Not recommended]
    actorSystem.terminate()
  }
  object Person{
    def happy(): Behavior[String]= Behaviors.receive{
      (context,message)=>
        context.log.info(s"I've received $message")
        Behaviors.same
    }
    def sad(): Behavior[String]= Behaviors.receive{
      (context,message)=>
        context.log.info(s"I've received $message")
        Behaviors.same
    }
    def apply(): Behavior[String]= happy()
  }
  def main(args: Array[String]): Unit = {
    demoSimpleActor("I love Akka!")
    demoSimpleActor("I love Scala!")
  }
}
