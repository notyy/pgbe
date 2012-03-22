package pgbe.util
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec

class StringUtilSpec extends FlatSpec with ShouldMatchers {
  "compact" should "make a mutiline string to one line and trim out spaces" in {
    val s =
      """
	  {
	"ret":0,
	"msg":""
       }
	  """
    StringUtil.compact(s) should be === """{"ret":0,"msg":""}"""
  }
}