package Basic

object practice1 extends App{

  (1 to 100).foreach{i =>
    println(s"Try $i -> fail: ${failedFunction()}, success: ${improvedFunction()}")
  }

  def improvedFunction():Int={
    val account= new BankAccount(2000)
    val depositThreads= (1 to 1000).map(_ => new Thread(() => account.deposit(1)))
    val withdrawThreads= (1 to 1000).map(_ => new Thread(() => account.withdraw(1)))
    (depositThreads++withdrawThreads).foreach(_.start())
    Thread.sleep(1000)
    account.getAmount
  }

  def failedFunction():Int={
    val account= new Bank_account(2000)
    val depositThreads= (1 to 1000).map(_ => new Thread(() => account.deposit(1)))
    val withdrawThreads= (1 to 1000).map(_ => new Thread(() => account.withdraw(1)))
    (depositThreads++withdrawThreads).foreach(_.start())
    Thread.sleep(1000)
    account.getAmount
  }

}
class BankAccount(/*@volatile*/ private var amount : Int){ // not a good thing to use volatile
  override def toString =s"$amount"

  def withdraw(money: Int): Unit = synchronized{this.amount -= money}

  def deposit(money: Int): Unit = synchronized{this.amount += money}

  def getAmount:Int= amount

}

class Bank_account(/*@volatile*/ private var amount : Int){ // not a good thing to use volatile
  override def toString =s"$amount"

  def withdraw(money: Int): Unit = this.amount -= money

  def deposit(money: Int): Unit = this.amount += money

  def getAmount:Int= amount

}