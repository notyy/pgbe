package pgbe.util.oauth
import net.liftweb.json._
import pgbe.util.Config
import net.liftweb.common.Box
import net.liftweb.common.Failure
import net.liftweb.common.Empty
import net.liftweb.common.Full
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pgbe.util.LogUtil._
import net.liftweb.util.Props

/**
 * @param clientId or called appId or oauth_consumer_key , it's same thing...
 * @param clientSecret or called appKey or simply "Key"
 * @param redirectUrl
 * @param state
 */
class QQService(val clientId: String, val clientSecret: String, val state: String) extends CommonOAuthService {
  val logger = LoggerFactory.getLogger(this.getClass)
  val endPointForAuthorizationCode: EndPoint = new EndPoint("https", "graph.qq.com", "/oauth2.0/authorize", Verb.GET)
  val endPointForAccessToken: EndPoint = new EndPoint("https", "graph.qq.com", "/oauth2.0/token", Verb.GET)
  var redirectUri: Option[String] = None
  var verifyCode: Option[String] = None
  var accessToken: Option[Token] = None

  def this(clientId: String, clientSecret: String) = {
    this(clientId, clientSecret, "")
  }

  def extractAccessToken(rawResp: Option[String]): Option[Token] = None
}