package pgbe.util.oauth
import org.slf4j.LoggerFactory
import pgbe.util.LogUtil._

object ResponseExtractor {
  val logger = LoggerFactory.getLogger(ResponseExtractor.getClass())

  def extractAuthCode(rawCode: String, msg: String): Option[String] = {
    logger.info("extracting auth code from:code=" + rawCode + ",msg=" + msg)
    if (rawCode.length() <= 6 || !msg.isEmpty()) logV(logger.warn)("get error authcodecode=" + rawCode + ",msg=" + msg, None)
    else Some(rawCode)
  }

  def extractToken(resp: Option[String]): Option[Token] = {
    logger.info("extracting token from:" + resp)
    val AccRegex = """(.*)=(.*)&(.*)=(\d*)""".r
    val ErrRegex = """(.*)"error":(.*),"error_description":"(.*)"}(.*)""".r
    resp match {
      case None => logV(logger.info)("", None)
      case Some(source) => source match {
        case AccRegex(_, accToken, _, expire) => logV(logger.info)("token extracted:", Some(Token(accToken, expire.toLong)))
        case ErrRegex(_, errCode, errMsg, _) => logV(logger.info)("server respond with error:code=" + errCode + ",msg=" + errMsg, None)
        case _ => logV(logger.error)("Strage resonse from server:" + source, None)
      }
    }
  }

}