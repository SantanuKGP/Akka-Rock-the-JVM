package actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}

object ActorsIntro6 {
  def test():Unit={
    // Step 2 : instantiate
    val person = ActorSystem(weird(),"Person")

    // Step 3 : communicate
    person ! 34 // asynchronously sends message
    person ! "Akka is bad."
//    person ! '\t'

    // Step 4 : Shut Down [ Not recommended]
    person.terminate()
  }
  object weird{
    def apply():Behavior[Any]=Behaviors.receive{
      (context,message) =>
        message match {
          case number:Int => context.log.info(s"A number :: $number")
          case text : String => context.log.info(s"I got a string :: $text")
        }
        Behaviors.same

    }
  }
  def main(args: Array[String]): Unit = {
    test()
  }
}
