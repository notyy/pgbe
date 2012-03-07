package pgbe.util.oauth
import org.scribe.oauth.OAuthService
import org.scribe.builder.ServiceBuilder
import org.scribe.model.SignatureType
import java.util.Scanner
import org.scribe.model.Verifier
import org.scribe.model.OAuthRequest
import org.scribe.model.Verb
import org.scribe.model.Token
import net.liftweb.json._
import net.liftweb.common.Logger
import pgbe.util.Config
import net.liftweb.common.Box
import net.liftweb.common.Failure
import net.liftweb.common.Empty

object QQService extends Logger {
  val qqApiKey = Config.qqApiKey.openOr("")
  val qqApiSecret = Config.qqApiScret.openOr("")

  def initService(callBackURI: String): OAuthService = {
    (new ServiceBuilder().provider(new QQApi())).
      apiKey(qqApiKey).apiSecret(qqApiSecret).callback(callBackURI).
      signatureType(SignatureType.QueryString).debug().build()
  }

  //这个函数从外部传入参数而不是从配置
  def initService(apiKey: String, apiSecret: String, callBackURI: String): OAuthService = {
    (new ServiceBuilder().provider(new QQApi())).
      apiKey(apiKey).apiSecret(apiSecret).callback(callBackURI).
      signatureType(SignatureType.QueryString).debug().build()
  }

  def requestAuthUrl(service: OAuthService) = {
    service.getAuthorizationUrl(null)
  }

  def requestAccessToken(service: OAuthService, verifyCode: String) = {
    service.getAccessToken(null, new Verifier(verifyCode));
  }

  def requestOpenId(service: OAuthService, accessToken: Token) = {
    val request = new OAuthRequest(Verb.GET, "https://graph.qq.com/oauth2.0/me");
    service.signRequest(accessToken, request);
    val response = request.send()
    response.getBody()
  }

  def requestUserInfo(service: OAuthService, accessToken: Token, openId: String, apiKey: String) = {
    val request = new OAuthRequest(Verb.GET,
      "https://graph.qq.com/user/get_user_info?oauth_consumer_key=" + apiKey + "&openid=" + openId)
    service.signRequest(accessToken, request)
    info("requestURL for user info is :\n" + request.toString())
    val response = request.send()
    extractQQUserInfo(response.getBody())
  }

  def extractOpenId(callback: String) = {
    val t1 = callback.drop(callback.indexOf("{"))
    val t2 = t1.take(t1.indexOf("}") + 1)
    ((parse(t2) \ "openid")).asInstanceOf[JString].s
  }

  def extractQQUserInfo(userInfoResp: String): Option[QQUserInfo] = {
    val jsonUser = parse(userInfoResp)
    val retCode = (jsonUser \ "ret").asInstanceOf[JInt].num
    val msg = (jsonUser \ "msg").asInstanceOf[JString].s
    if (retCode == 0) { //0 means success
      Some(QQUserInfo(
        (jsonUser \ "nickname").asInstanceOf[JString].s,
        (jsonUser \ "figureurl").asInstanceOf[JString].s,
        (jsonUser \ "figureurl_1").asInstanceOf[JString].s,
        (jsonUser \ "figureurl_2").asInstanceOf[JString].s,
        (jsonUser \ "gender").asInstanceOf[JString].s))
    } else {
      warn("request userInfo failed,with ret code:" + retCode + ",msg:" + msg)
      Empty
    }
  }

}