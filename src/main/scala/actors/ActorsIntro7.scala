package actors

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object ActorsIntro7 {
  def test():Unit={
    // Step 2 : instantiate
    val person = ActorSystem(betterActor(),"Person")

    // Step 3 : communicate
    person ! betterActor.IntMessage(34)  // asynchronously sends message
    person ! betterActor.StringMessage("Akka is bad.")
    //    person ! '\t'

    // Step 4 : Shut Down [ Not recommended]
    person.terminate()
  }
  object betterActor{
    trait Message
    case class IntMessage(number: Int) extends Message
    case class StringMessage(string: String) extends Message
    def apply():Behavior[Message]=Behaviors.receive{
      (context,message) =>
        message match {
          case IntMessage(number) => context.log.info(s"A number :: $number")
          case StringMessage(text)=> context.log.info(s"I got a string :: $text")
        }
        Behaviors.same

    }
  }
  def main(args: Array[String]): Unit = {
    test()
  }
}
