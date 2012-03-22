package pgbe.util.oauth.lift
import net.liftweb.http.SessionVar
import net.liftweb.common.Box
import net.liftweb.common.Empty
import pgbe.util.oauth.model.User
import net.liftweb.http.S
import net.liftweb.http.provider.HTTPCookie
import pgbe.util.Config
import pgbe.util.oauth.QQService
import net.liftweb.common.Full
import pgbe.util.oauth.Token
import pgbe.util.oauth.UID
import pgbe.util.oauth.VerifyCode

object userVar extends SessionVar[Box[User]](Empty)

class OAuthRenew {
  private val LAST_LOGIN_FROM = "kaopua.lastLoginFrom" //QQ or Sina
  private val OPENID = "kaopua.openId"
  private val TOKEN = "kaopua.token"
  private val lastLoginFrom = S.findCookie(LAST_LOGIN_FROM)
  private val openIdCookie = S.findCookie(OPENID)
  private val tokenCookie = S.findCookie(TOKEN)
  private val code = S.param("code")
  private val state = S.param("state")
  private val hostDomain = Config.hostDomainVar
  private val callBackUrl = hostDomain + S.uri
  private lazy val qqService = {
    if (openIdCookie.isEmpty || openIdCookie.get.value.isEmpty || openIdCookie.get.value.get.isEmpty ||
      tokenCookie.isEmpty || tokenCookie.get.value.isEmpty || tokenCookie.get.value.get.isEmpty)
      new QQService(Config.qqApiKeyVar, Config.qqApiScretVar, callBackUrl, None, None)
    else new QQService(Config.qqApiKeyVar, Config.qqApiScretVar, callBackUrl,
      Some(openIdCookie.get.value.get), Some(tokenCookie.get.value.get))
  }

  def identifyUser_!(): Box[User] = (userVar.is, lastLoginFrom, openIdCookie, tokenCookie, code) match {
    case (Full(user), _, _, _, _) => Full(user)
    case (Empty,
      Full(HTTPCookie(LAST_LOGIN_FROM, Full(lastLoginFrom), _, _, _, _, _, _)),
      Full(HTTPCookie(OPENID, Full(openId), _, _, _, _, _, _)),
      Full(HTTPCookie(TOKEN, Full(token), _, _, _, _, _, _)), _) => {
      //TODO: should test last login to choose qqService or other service
      renewQQUserInfo(qqService.requestUserInfo2_!(Token(token, 30000), UID(openId)))

    }
    case (Empty, Empty, Empty, Empty, Full(sCode)) => {
      renewQQUserInfo(qqService.requestUserInfoAll_!(VerifyCode(sCode)))
    }
    case _ => Empty
  }

  def makeAuthUrl = qqService.makeAuthUrl

  def renewQQUserInfo(user: Option[User]): Box[User] = {
    if (user.isEmpty) Empty
    else {
      userVar.set(Full(user.get))
      setCookie_!(user.get.comeFrom, user.get.openId.get, user.get.accessToken.get)
      Full(user.get)
    }
  }

  def setCookie_!(loginFrom: String, openId: String, token: String) = {
    val lastLoginFromCookie = HTTPCookie(LAST_LOGIN_FROM, loginFrom)
    lastLoginFromCookie.setDomain(hostDomain)
    lastLoginFromCookie.setMaxAge(3600 * 24 * 7) //保留cookie一周
    val openIdCookie = HTTPCookie(OPENID, openId)
    openIdCookie.setDomain(hostDomain)
    openIdCookie.setMaxAge(3600 * 24 * 7) //保留cookie一周
    val tokenCookie = HTTPCookie(TOKEN, token)
    openIdCookie.setDomain(hostDomain)
    openIdCookie.setMaxAge(3600 * 24 * 7) //保留cookie一周
    S.addCookie(openIdCookie)
    S.addCookie(tokenCookie)
  }
}