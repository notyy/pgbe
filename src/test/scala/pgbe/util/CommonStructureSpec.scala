package pgbe.util
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import CommonStructure._
import org.slf4j.LoggerFactory
import pgbe.util.LogUtil._

class CommonStructureSpec extends FlatSpec with ShouldMatchers {
  val logger = LoggerFactory.getLogger(this.getClass())
  implicit val iLog = logger
  def f[T](para: T) = Some(para)

  def filter[T](param: T): Option[T] = param match {
    case s: String if s == "error" => None
    case p => Some(p)
  }

  val add1 = (x: Int) => Some(x + 1)
  val minus2 = (x: Int) => Some(x - 2)
  val div1 = (x: Int) => Some(x / 1)
  val div0: Int => Option[Int] = (_: Int) => { logger.error("div by 0"); None }

  "Monadic" should "use thenDo to connect a A => Option[B] and B => Option[C] and " +
    "make it a A => Option[C]" in
    {
      thenDo(add1)(minus2)(5) should be === Some(4)
      thenDo(add1)(div0)(5) should be === None
      thenDo(div0)(add1)(5) should be === None
      //now using implicit version 
      import pgbe.util.CommonStructure._ //import the implicit conversion
      (add1 then minus2 then div1)(5) should be === Some(4)
      (add1 then minus2 then div0)(5) should be === None
      (add1 then div0 then minus2)(5) should be === None
      //try Do's apply function
      val calc = Do("加上1", add1) then Do("减去2", minus2) then Do("除以1", div1)
      calc(5) should be === Some(4)
      val calcNone = Do("加上1", add1) then Do("除以0", div0) then Do("减去2", minus2)
      calcNone(5) should be === None
    }

  "Do case class" should "apply function to param" in {
    //even if desc is null, it should not error!
    val doit = Do(null, add1)
    doit(5) should be === Some(6)
  }
}