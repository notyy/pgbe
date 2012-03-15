package pgbe.util
import org.slf4j.Logger

object LogUtil {
  private def kestrel[A](x: A)(f: A => Unit): A = { f(x); x }

  /**
   * this function output s:x as log information,and keeps value x as result
   * @param <A>
   * @param f
   * @param s
   * @param x
   * @return original value x
   */
  def logV[A](f: String => Unit)(s: String, x: A) = kestrel(x) { y => f(s + ": " + y) }

  /**
   * this function only output s,doesn't output value x
   * @param <A>
   * @param f
   * @param s
   * @param x
   * @return original value x
   */
  def logVP[A](f: String => Unit)(s: String, x: A) = kestrel(x) { y => f(s) }
}