package childActors

import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object childActors1 {

  def test():Unit={
    val userGuardianBehavior: Behavior[Unit]= Behaviors.setup{ context=>
      val parent = context.spawn(Parent(),"Parent-Child-0")
      parent ! Parent.CreateChild("alice")
      parent ! Parent.CreateChild("bob")
      parent ! Parent.TellChild("alice","Hey kid! you there?")
      parent ! Parent.TellChild("Daniel","I hope your Akka skills are good")
      parent ! Parent.stopChild("alice")
      parent ! Parent.TellChild("alice","Are you still there?")
      Behaviors.empty
    }
    val system= ActorSystem(userGuardianBehavior,"Parent-Child-test-2")
    system.terminate()
  }

  object Parent{
    trait Command
    case class CreateChild(name: String) extends Command
    case class TellChild(name: String,message: String) extends Command
    case class stopChild(name: String) extends Command

    def apply():Behavior[Command]= active(Map())

    def active(children : Map[String,ActorRef[String]]):Behavior[Command]=Behaviors.receive{ (context,message)=>
      message match {
        case CreateChild(name) =>
          context.log.info(s"[Parent] creating child $name")
          val childRef= context.spawn(Child(),name)
          active(children + (name-> childRef))

        case TellChild(name,message) =>
          val childOption= children.get(name)
//          childOption.foreach(child => child! message)
          childOption.fold(context.log.info(s"child $name could not be found."))(child => child! message)
//          context.log.info(s"[Parent to child $name] : Sending message $message")
          Behaviors.same

        case stopChild(name) =>
          context.log.info(s"[Parent] Attempting to stop the child with $name")
          val childOption= children.get(name)
          childOption.fold(context.log.info(s"child $name could not be stopped."))(context.stop)
          active(children-name)
        case _ => context.log.info("[Parent] : Message not supported")
          Behaviors.same
      }
    }
  }
  object Child {
    def apply(): Behavior[String] = Behaviors.receive{ (context,message)=>
      context.log.info(s"[${context.self.path.name}] received $message")
      Behaviors.same
    }
  }

  def main(args: Array[String]): Unit = {
    test()
  }
}
