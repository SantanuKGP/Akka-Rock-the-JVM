package actors

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object ActorsIntro5 {

  def testPerson():Unit={
    // Step 2 : instantiate
    val person = ActorSystem(Person(),"Person")

    // Step 3 : communicate
    person ! "I love the color yellow" // asynchronously sends message
    person ! "Akka is bad."
    person ! "I love color red!"
    person ! "Akka is awesome!"
    person ! "I love Akka!"
    // Step 4 : Shut Down [ Not recommended]
    person.terminate()
  }
  object Person{
    def happy(): Behavior[String]= Behaviors.receive{
      (context,message)=>
        message match{
          case "Akka is bad." =>
            context.log.info("Don't say anything bad about Akka!!")
            sad()
          case _ =>
            context.log.info(s"[Happy] : I've received $message")
            Behaviors.same
        }

    }
    def sad(): Behavior[String]= Behaviors.receive{
      (context,message)=>
        message match{
          case "Akka is awesome!" =>
            context.log.info("Happy now!!")
            happy()
          case _ =>
            context.log.info(s"[Sad] : I've received $message")
            Behaviors.same
        }
    }
    def apply(): Behavior[String]= happy()
  }
  def main(args: Array[String]): Unit = {
    testPerson()
  }

}
