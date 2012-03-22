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
import pgbe.util.oauth.model.User
import pgbe.util.CommonStructure._
import pgbe.util.StringUtil

/**
 * @param clientId or called appId or oauth_consumer_key , it's same thing...
 * @param clientSecret or called appKey or simply "Key"
 * @param redirectUrl
 * @param state
 */
class QQService(val clientId: String, val clientSecret: String, val redirectUri: String,
  val openId: Option[String], val accessToken: Option[String]) extends CommonOAuthService {
  val logger = LoggerFactory.getLogger(this.getClass)
  val state = "QQ" //user is from QQ
  val endPointForAuthorizationCode: EndPoint = new EndPoint("https", "graph.qq.com", "/oauth2.0/authorize", Verb.GET)
  val endPointForAccessToken: EndPoint = new EndPoint("https", "graph.qq.com", "/oauth2.0/token", Verb.GET)
  val endPointForOpenId: EndPoint = new EndPoint("https", "graph.qq.com", "/oauth2.0/me", Verb.GET)
  val endPointForUserInfo: EndPoint = new EndPoint("https", "graph.qq.com", "/user/get_user_info", Verb.GET)
  val extractor = QQResponseExtractor

  def requestUserInfoAll_! : (VerifyCode => Option[User]) = Do("request accessToken", requestAccessToken_!) then
    Do("request openId", requestOpenId_!) then
    Do("request userInfo", requestUserInfoT_!)

  def requestUserInfoT_! = (requestUserInfo2_! _).tupled

  def requestUserInfo2_!(token: Token, openId: UID): Option[User] = {
    val req = Request(endPointForUserInfo,
      Param("access_token", token.accessToken),
      Param("oauth_consumer_key", clientId),
      Param("openid", openId.value))
    val impl = Do("building request to getUserInfo", reqBuilder.build) then
      Do("connect and send request", connector.send_!(_: String, endPointForUserInfo.verb)) then
      Do("", extractor.extractUserInfo)
    impl(req).map(u => { u.openId = Some(openId.value); u.accessToken = Some(token.accessToken); u })
  }

  private def requestOpenId_!(token: Token): Option[(Token, UID)] = {
    val req = Request(endPointForOpenId,
      Param("access_token", token.accessToken))
    val impl = Do("building request to getOpenId", reqBuilder.build) then
      Do("", connector.send_!(_: String, endPointForOpenId.verb)) then
      Do("extract openId", extractor.extractOpenId)
    impl(req).map(openId => (token, openId))
  }

  object QQResponseExtractor extends CommonResponseExtractor {
    def extractOpenId(resp: String): Option[UID] = {
      val OpenIdRegex = """.*?"openid":"(.*?)"}.*?""".r
      StringUtil.compact(resp) match {
        case OpenIdRegex(openId) => Some(UID(openId))
        case _ => None
      }
    }

    def extractUserInfo(resp: String): Option[User] = {
      val UserInfoRegex = """.*"nickname":"(.*?)","figureurl":"(.*?)",.*?""".r
      StringUtil.compact(resp) match {
        case UserInfoRegex(nickName, figureUrl) => Some(User("QQ", nickName, figureUrl, None, None))
        case _ => None
      }
    }

  }
}