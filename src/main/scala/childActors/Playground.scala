package childActors

import akka.actor.typed.ActorSystem

object Playground extends App{

  val actorSystem= akka.actor.ActorSystem("Hello_Akka")
  println(actorSystem.name)

}
