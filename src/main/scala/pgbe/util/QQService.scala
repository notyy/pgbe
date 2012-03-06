package pgbe.util
import org.scribe.oauth.OAuthService
import org.scribe.builder.ServiceBuilder
import org.scribe.model.SignatureType
import java.util.Scanner
import org.scribe.model.Verifier
import org.scribe.model.OAuthRequest
import org.scribe.model.Verb
import org.scribe.model.Token

object QQService {
  val scanner = new Scanner(System.in)
  val qqApiKey = Config.qqApiKey.openOr("")
  val qqApiSecret = Config.qqApiScret.openOr("")
  var service: OAuthService = null

  def initService(callBackURI: String) = {
    service = (new ServiceBuilder().provider(new QQApi())).
      apiKey(qqApiKey).apiSecret(qqApiSecret).callback(callBackURI).
      signatureType(SignatureType.QueryString).build()
  }

  def requestAuthUrl(callBackURI: String) = {
    initService(callBackURI)
    service.getAuthorizationUrl(null)
  }

  def requestAccessToken(verifyCode: String) = {
    service.getAccessToken(null, new Verifier(verifyCode));
  }

  def requestOpenId(accessToken: Token) = {
    val request = new OAuthRequest(Verb.GET, "https://graph.qq.com/oauth2.0/me");
    service.signRequest(accessToken, request);
    val response = request.send()
    response.getBody()
  }

  def requestUserInfo(accessToken: Token, openId: String) = {
    val request = new OAuthRequest(Verb.GET, "https://graph.qq.com/user/get_user_info?openid=" + openId);
    service.signRequest(accessToken, request)
    val response = request.send()
    response.getBody()
  }

}