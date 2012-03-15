package pgbe.util
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.slf4j.LoggerFactory

class LogUtilSpec extends FlatSpec with ShouldMatchers {
  val logger = LoggerFactory.getLogger(this.getClass())
  import LogUtil._

  "LogUtil" should "print log info and keep the value, and the calc for value should only be called once" in {
    def calcValue() = { println("calcValue"); 100 } // to confirm it's called only once 
    val v = logV(logger.info)("result is", calcValue())
    v should be === 100
    val p = logVP(logger.info)("operation success", calcValue())
    p should be === 100
  }
}