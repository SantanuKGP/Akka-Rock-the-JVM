package childActors


import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors


object childActors {

  def test():Unit={
    val parent = ActorSystem(Parent(),"Parent-Child")

    parent ! Parent.CreateChild("Child-0")
    parent ! Parent.TellChild("Hey kid! you there?")
    parent ! Parent.stopChild
    parent ! Parent.CreateChild("child2")
    parent ! Parent.TellChild("yo new kid, how are you?")

    parent.terminate()
  }

  object Parent{
    trait Command
    case class CreateChild(name: String) extends Command
    case class TellChild(message: String) extends Command
    case object stopChild extends Command

    def apply():Behavior[Command]= Behaviors.receive{
      (context,message)=>
        message match{
          case CreateChild(name)=>
            context.log.info(s"[Parent] : Creating child with name $name")
            val childRef: ActorRef[String] = context.spawn(Child(), name)
            active(childRef)
        }
    }

    def active(childRef : ActorRef[String]):Behavior[Command]=Behaviors.receive{ (context,message)=>
        message match {
          case TellChild(message) =>
            context.log.info(s"[Parent to child] : Sending message $message")
            childRef ! message
            Behaviors.same

          case stopChild =>
            context.log.info("[Parent] Stopping the child")
            context.stop(childRef)
            apply()

          case _ => context.log.info("[Parent] : Message not supported")
            Behaviors.same
        }
//      println(childRef.path)


    }
  }
  object Child {
    def apply(): Behavior[String] = Behaviors.receive{ (context,message)=>
      context.log.info(s"[Child] received $message")
      Behaviors.same
    }
  }

  def main(args: Array[String]): Unit = {
    test()
  }
}
