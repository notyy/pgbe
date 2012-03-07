package pgbe.util.oauth
import net.liftweb.common.Logger
import net.liftweb.json._

object QQLoginTest extends Logger {
  //这个不是自动化单元测试，而是手工运行的
  def testFromCode(apiKey: String, apiSecret: String, code: String, callback: String) {
    val qqService = QQService.initService(apiKey, apiSecret, callback)
    info("从已经得到 code开始:" + code)
    info("现在获取token")
    val token = QQService.requestAccessToken(qqService, code)
    info("token为:" + token)
    info("现在获取openid")
    val openIdResp = QQService.requestOpenId(qqService, token)
    info("收到的原始响应为:" + openIdResp)
    val openId = QQService.extractOpenId(openIdResp)
    info("解出的 openId 为:\n" + openId)
    info("现在获取userInfo")
    val userInfoResp = QQService.requestUserInfo(qqService, token, openId, apiKey)
    info("收到的原始响应为:" + userInfoResp)
  }
}