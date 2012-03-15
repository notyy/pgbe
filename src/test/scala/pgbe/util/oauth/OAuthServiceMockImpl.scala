package pgbe.util.oauth
// this is used as a fixture
import org.slf4j.LoggerFactory

class OAuthServiceMockImpl extends CommonOAuthService {
  val logger = LoggerFactory.getLogger(this.getClass())
  val endPointForAuthorizationCode: EndPoint = EndPoint("https", "provider.sample", "/authorize", Verb.GET)
  val endPointForAccessToken: EndPoint = EndPoint("https", "provider.sample", "/token", Verb.GET)
  val clientId = "clientId_01234"
  val clientSecret = "clientSecret_ABCD"
  var redirectUri: Option[String] = Some("http://mysite.sample/callback")
  val state = "from_some_provider"
  var verifyCode: Option[String] = None
  var accessToken: Option[Token] = None
  def extractAccessToken(rawResp: Option[String]): Option[Token] = None
}
