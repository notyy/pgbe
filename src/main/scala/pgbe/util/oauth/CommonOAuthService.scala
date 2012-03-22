package pgbe.util.oauth
import pgbe.util.LogUtil._
import pgbe.util.oauth.model.User
import org.slf4j.Logger
import pgbe.util.CommonStructure._

abstract trait CommonOAuthService extends OAuthService {
  // implementations should provide following vals
  val logger: Logger
  lazy implicit val iLog = logger
  val endPointForAuthorizationCode: EndPoint
  val endPointForAccessToken: EndPoint
  val clientId: String
  val clientSecret: String
  val state: String
  val redirectUri: String
  //TODO: scope field should be implemented later

  def makeAuthUrl: Option[String] = {
    assert(!redirectUri.isEmpty)
    logger.info("making AuthUrl")
    val req = new Request(endPointForAuthorizationCode,
      Param("client_id", clientId),
      Param("redirect_uri", redirectUri),
      Param("response_type", "code"),
      Param("state", state))
    OAuthRequestBuilder.build(req)
  }

  def requestAccessToken_! : VerifyCode => Option[Token] = { verifyCode =>
    assert(!clientId.isEmpty()); assert(!clientSecret.isEmpty());
    assert(!verifyCode.code.isEmpty); assert(redirectUri != null); assert(state != null)
    val req = Request(endPointForAccessToken,
      Param("client_id", clientId),
      Param("client_secret", clientSecret),
      Param("code", verifyCode.code),
      Param("grant_type", "authorization_code"),
      Param("redirect_uri", redirectUri),
      Param("state", state))
    val impl = Do("building request to accessToken", reqBuilder.build) then
      Do("connect and send request to " + endPointForAccessToken, connector.send_!(_: String, endPointForAccessToken.verb)) then
      Do("extract accessToken from response", extractAccessToken)
    impl(req)
  }

  private def extractAccessToken(rawResp: String): Option[Token] = responseExtractor.extractToken(rawResp)

  def requestUserInfoAll_! : VerifyCode => Option[User]
}