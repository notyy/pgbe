package pgbe.util.oauth
import org.slf4j.LoggerFactory
import pgbe.util.LogUtil._
import pgbe.util.oauth.model.User
import pgbe.util.CommonStructure._
import pgbe.util.StringUtil

trait CommonResponseExtractor {
  val logger = LoggerFactory.getLogger(this.getClass())
  implicit val iLog = logger

  def filter(param: String): Option[String] = {
    val ErrRegex = """(.*?)"error":(.*?),"error_description":"(.*?)"}(.*?)""".r
    val RetCodeRegex = """(.*?)"ret":(\d+),(.*?)""".r
    val pOneLine = StringUtil.compact(param)
    pOneLine match {
      case ErrRegex(_, _, _, _) => None
      case RetCodeRegex(_, ret, _) if (ret != "0") => None
      case p => Some(param)
    }
  }

  def extractAuthCode(rawCode: String, msg: String): Option[String] = {
    if (rawCode.length() <= 6 || !msg.isEmpty()) logV(logger.error)("get error authcodecode=" + rawCode + ",msg=" + msg, None)
    else Some(rawCode)
  }

  def extractToken =
    Do("filter out error resp from server", filter) then Do("extract token", _extractToken)

  private def _extractToken(resp: String): Option[Token] = {
    val AccRegex = """.*?=(.*?)&.*?=(\d*?)""".r
    StringUtil.compact(resp) match {
      case AccRegex(accToken, expire) => Some(Token(accToken, expire.toLong))
      case _ => None
    }
  }
}