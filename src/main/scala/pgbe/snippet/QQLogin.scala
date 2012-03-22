package pgbe.snippet

import scala.xml.{ NodeSeq }
import net.liftweb.util.Helpers._
import net.liftweb.http.S
import pgbe.util.Config
import pgbe.util.oauth._
import net.liftweb.common.Full
import scala.xml.Text
import Config._
import org.slf4j.LoggerFactory
import pgbe.util.oauth.lift.OAuthRenew

class QQLogin {
  val logger = LoggerFactory.getLogger(this.getClass())
  val pageUrl = S.uri
  val code = S.param("code")
  val state = S.param("state")
  val hostDomain = hostDomainVar
  val callBack = hostDomain + pageUrl + "?state=test" // + ppId(eId)
  val oAuthRenew = new OAuthRenew

  def testDo = logger.info("ok,I do sth")

  def render = "a [href]" #> { in: NodeSeq =>
    logger.info("code=" + code)
    (code, state) match {
      case (Full(sCode), _) => logger.info("CODE:" + sCode); S.warning("CODE:" + sCode); Text(oAuthRenew.makeAuthUrl.get)
      case _ => Text(oAuthRenew.makeAuthUrl.get)
    }
  }
}