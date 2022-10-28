package childActors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors

import scala.collection.mutable.{Map => MutableMap}

object BreakingActorEncapsulation {
  trait AccountCommand
  case class Deposit(cardId: String, amount: Double) extends AccountCommand
  case class Withdraw(cardId: String, amount: Double) extends AccountCommand
  case class CreateCreditCard(cardId: String) extends AccountCommand
  case object CheckCardStatus extends AccountCommand

  trait CreditCardCommand
  case class AttachToAccount(balances: MutableMap[String,Double], cards: MutableMap[String, ActorRef[CreditCardCommand]]) extends CreditCardCommand
  case object CheckStatus extends CreditCardCommand

  object NaiveBankAccount{

    def apply(): Behavior[AccountCommand] =Behaviors.setup{ context=>
      val accountBalances: MutableMap[String, Double]= MutableMap()
      val cardMap: MutableMap[String, ActorRef[CreditCardCommand]] = MutableMap()

      Behaviors.receiveMessage{
        case CreateCreditCard(cardId)=>
          context.log.info(s"Creating card $cardId")
          val creditCardRef = context.spawn(CreditCard(cardId),cardId)
          accountBalances += cardId ->10
          creditCardRef ! AttachToAccount(accountBalances,cardMap)
          Behaviors.same

        case Deposit(cardId, amount) =>
          val oldBalance: Double = accountBalances.getOrElse(cardId,0)
          context.log.info(s"Depositing $amount to card $cardId, balance: ${oldBalance+amount}")
          accountBalances += cardId ->(oldBalance+amount)
//          accountBalances() += cardId ->(oldBalance+amount)
          Behaviors.same

        case Withdraw(cardId, amount) =>
          val oldBalance: Double = accountBalances.getOrElse(cardId,0)
          if(oldBalance < amount)
            context.log.warn(s"Attempted withdrawal of $amount via card $cardId: insufficient balance")
          else {
            context.log.info(s"Withdrawing $amount via card $cardId, balance: ${oldBalance-amount}")
            accountBalances += cardId ->(oldBalance-amount)
          }
          Behaviors.same
        case CheckCardStatus=>
          context.log.info("Checking status of all cards")
          cardMap.values.foreach(cardRef => cardRef!CheckStatus)
          Behaviors.same
      }

    }
  }

  object CreditCard {
    def apply(cardId: String): Behavior[CreditCardCommand] = Behaviors.receive { (context, message) =>
      message match {
        case AttachToAccount(balances, cards) =>
          context.log.info(s"[$cardId] Attaching to bank account")
          balances += cardId -> 20
          cards += cardId -> context.self
          Behaviors.same
        case CheckStatus =>
          context.log.info(s"[$cardId] All things green.")
          Behaviors.same
      }
    }
  }
  def main(args: Array[String]): Unit = {
    val userGuardian: Behavior[Unit] = Behaviors.setup { context =>
      val bankAccount = context.spawn(NaiveBankAccount(), "bankAccount")

      bankAccount ! CreateCreditCard("gold")
      bankAccount ! CreateCreditCard("premium")
      bankAccount ! Deposit("gold", 1000)
      bankAccount ! CheckCardStatus

      Behaviors.empty
    }

    val system = ActorSystem(userGuardian, "DemoNaiveBankAccount")
    Thread.sleep(1000)
    system.terminate()
  }
}
