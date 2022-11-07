package Infrastructure
trait A {  def hello(): String = "Hello from A"}
trait B extends A {  override def hello(): String = "Hello from B"}
trait C extends A {  override def hello(): String = "Hello from C"}
trait D extends A {  override def hello(): String = "Hello from D"}

trait E extends C with B with D{  def hello(): String}
trait F extends B with D with C{  def hello(): String}
trait G extends D with C with B{  def hello(): String}

class Print1 extends E{
  println(hello())
}

class Print2 extends F{
  println(hello())
}

class Print3 extends G{
  println(hello())
}

object queryFromNet extends App{

  new Print1 // D
  new Print2 // C
  new Print3 // B
  /*
  (1 to 10).foreach(_ => new Print1) // will print D
  (1 to 10).foreach(_ => new Print2) // will print C
  (1 to 10).foreach(_ => new Print3) // will print B
  */
}
