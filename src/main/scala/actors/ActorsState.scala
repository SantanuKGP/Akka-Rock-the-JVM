package actors

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object ActorsState {
  def test():Unit={
    val system= ActorSystem(wordCount(),"Counting-words")

    system ! "I like Akka."
    system ! "I want to learn Akka."
    system ! "Good Bye, Friend!!"

    system.terminate()
  }
  object wordCount{
    def apply():Behavior[String]= wc(0)
    def wc(count : Int):Behavior[String]=Behaviors.receive{
      (context,message)=>
        val l= message.split(" ").length
        context.log.info(s"[Message]: $message, word count till now: ${count+l}")
        wc(count+l)
    }
  }
  def main(args: Array[String]): Unit = {
    test()
  }
}
