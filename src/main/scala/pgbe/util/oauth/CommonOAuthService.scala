package pgbe.util.oauth
import pgbe.util.LogUtil._
import pgbe.util.oauth.model.User
import org.slf4j.Logger

trait CommonOAuthService extends OAuthService {
  // implementations should provide following vals
  val logger: Logger
  val endPointForAuthorizationCode: EndPoint
  val endPointForAccessToken: EndPoint
  val clientId: String
  val clientSecret: String
  val state: String
  //TODO: scope field should be implemented later
  // some thing that's vary
  var redirectUri: Option[String]
  var verifyCode: Option[String]
  var accessToken: Option[Token]

  def makeAuthUrl: String = {
    assert(!redirectUri.isEmpty)
    logger.info("making AuthUrl")
    val req = new Request(endPointForAuthorizationCode,
      Param("client_id", clientId),
      Param("redirect_uri", redirectUri.get),
      Param("response_type", "code"),
      Param("state", state))
    logV(logger.info)("", OAuthRequestBuilder.build(req))
  }

  def requestAccessToken_! : Option[Token] = {
    assert(!clientId.isEmpty()); assert(!clientSecret.isEmpty());
    assert(!verifyCode.isEmpty); assert(!state.isEmpty()); assert(!redirectUri.isEmpty)
    val params = "requestAccessToken (endPoint:" + endPointForAccessToken + ",code:" + verifyCode + ",redirectUrl:" + redirectUri + ",state:" + state + ")"
    logger.info(params)
    val resp = connector.send_!(reqBuilder.build(Request(endPointForAccessToken,
      Param("clientId", clientId),
      Param("clientSecret", clientSecret),
      Param("code", verifyCode.get),
      Param("grant_type", "authorization_code"),
      Param("redirectUri", redirectUri.get),
      Param("state", state))), endPointForAccessToken.verb)
    logVP(logger.info)("requestAccessToken processing finished", extractAccessToken(resp))
  }

  def extractAccessToken(rawResp: Option[String]): Option[Token]

  def requestOpenId_!(accessToken: String): Option[String] = None
  def requestUserInfo_!(accessToken: String, openId: String, apiKey: String): Option[User] = None
}