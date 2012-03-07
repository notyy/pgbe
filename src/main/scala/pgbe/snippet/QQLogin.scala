package pgbe.snippet

import scala.xml.{ NodeSeq }
import net.liftweb.util.Helpers._
import net.liftweb.http.S
import pgbe.util.Config
import pgbe.util.oauth._
import net.liftweb.common.Full
import scala.xml.Text
import net.liftweb.common.Logger

class QQLogin extends Logger {
  val pageUrl = S.uri
  val code = S.param("code")
  val state = S.param("state")
  val hostDomain = Config.hostDomain.openOr("")
  val callBack = hostDomain + pageUrl + "?state=test" // + ppId(eId)
  val qqService = QQService.initService(callBack)

  def render = "a [href]" #> { in: NodeSeq =>
    info("code=" + code)
    (code, state) match {
      case (Full(sCode), _) => info("CODE:" + sCode); S.warning("CODE:" + sCode); Text(QQService.requestAuthUrl(qqService))
      case _ => Text(QQService.requestAuthUrl(qqService))
    }
  }
}