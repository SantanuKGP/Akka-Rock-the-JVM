package childActors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}


object childActors2{

  trait MasterProtocol
  trait WorkerProtocol
  trait UserProtocol
  case class Initialize(nChildren:Int) extends MasterProtocol
  case class WordCountTask(text: String, replyTo:ActorRef[UserProtocol]) extends MasterProtocol
  case class WordCountReply(id: Int,count:Int) extends MasterProtocol
  case class WorkerTask(id: Int, text: String) extends WorkerProtocol
  case class Reply(count: Int) extends UserProtocol


  object WordCounterMaster{
    def apply():Behavior[MasterProtocol]= Behaviors.receive{ (context, message)=>
      message match{
        case Initialize(nChildren)=>
          context.log.info(s"[master] initializing with $nChildren children")
          val childRefs = for(i <- 1 to nChildren) yield context.spawn(WordCounterWorker(context.self),s"worker$i")
          active(childRefs,0,0,Map())

        case _ => context.log.info("Command Not Supported : idle !")
          Behaviors.same
      }
    }
    def active( childRefs: Seq[ActorRef[WorkerProtocol]],
                currentChildIndex:Int,
                currentTaskId: Int,
                requestMap: Map[Int, ActorRef[UserProtocol]]):Behavior[MasterProtocol] = Behaviors.receive{(context,message)=>
      message match {
        case WordCountTask(text, replyTo)=>
          context.log.info(s"[master] I've received $text, sent to child $currentChildIndex")
          val task = WorkerTask(currentTaskId,text)
          val childRef= childRefs(currentChildIndex)
          childRef ! task
          val nextChildIndex= (currentChildIndex+1)% childRefs.length
          val nextTaskId= currentTaskId+1
          val newRequestMap = requestMap + (currentTaskId -> replyTo)
          active(childRefs,nextChildIndex,nextTaskId,newRequestMap)

        case WordCountReply(id, count)=>
          context.log.info(s"[Master] I've received a reply for task id $id with $count")
          val originalSender=requestMap(id)
          originalSender ! Reply(count)
          active(childRefs, currentChildIndex, currentTaskId, requestMap-id)

        case _ => context.log.info("Command Not Supported : active !")
        Behaviors.same
      }
    }
  }
  object WordCounterWorker{
    def apply(masterRef : ActorRef[MasterProtocol]):Behavior[WorkerProtocol]= Behaviors.receive{(context,message)=>
      message match{
        case WorkerTask(id, text) =>
          context.log.info(s"[${context.self.path}] I've received task $id with text: $text")
          val result = text.split(" ").length
          masterRef ! WordCountReply(id,result)
          Behaviors.same

        case _ => context.log.info(s"[${context.self.path}] Not Supported!!!")
          Behaviors.same
      }
    }
  }
  object Aggregator{
    def apply():Behavior[UserProtocol]= active()

    def active(totalWords:Int=0):Behavior[UserProtocol]=Behaviors.receive{(context,message)=>
      message match{
        case Reply(count) =>
          context.log.info(s"[Aggregator] : I've received $count, Total words: ${totalWords+count}")
          active(totalWords+count)

      }
    }
  }
  def testWordCounter():Unit={
    val userGuardian: Behavior[Unit]= Behaviors.setup{ context=>
      val aggregator= context.spawn(Aggregator(),"aggregator")
      val wcm= context.spawn(WordCounterMaster(),"master")

      wcm! Initialize(3)
      wcm! WordCountTask("I love akka",aggregator)
      wcm! WordCountTask("Scala is super dope", aggregator)
      wcm! WordCountTask("Yes it is", aggregator)
      wcm! WordCountTask("Testing round robin scheduling", aggregator)
      Behaviors.empty
    }
    val system=ActorSystem(userGuardian,"Word_Counting")
    system.terminate()
  }
  def main(args: Array[String]): Unit = {
    testWordCounter()
  }
}
