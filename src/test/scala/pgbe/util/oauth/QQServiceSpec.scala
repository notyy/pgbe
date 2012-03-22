package pgbe.util.oauth
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import java.util.Properties
import org.slf4j.LoggerFactory
import pgbe.util.oauth.model.User

class QQServiceSpec extends FlatSpec with ShouldMatchers {
  val logger = LoggerFactory.getLogger(this.getClass())
  "QQService" should "be able to acquire user info after authorization code is got" in {
    // this is not unit test, it will connect to QQ OAuth2 provider
    val shouldRun_? = true
    if (shouldRun_?) {
      val properties = new Properties
      properties.load(this.getClass().getResourceAsStream("/test.properties"))
      logger.debug("find properties:")
      properties.list(Console.out)
      val qqService = new QQService(
        properties.getProperty("apiKey"),
        properties.getProperty("apiSecret"),
        properties.getProperty("redirectUri"), None, None)
      val user = qqService.requestUserInfoAll_!(VerifyCode("6E48E23EB5D6924AB03FABC39635FC49")) //input your auth code from step1
      user should not be None
      user.get.nickName.isEmpty() should be === false
      user.get.figureUrl.isEmpty() should be === false
      user.get.openId should not be None
      user.get.accessToken should not be None
      logger.info("user info got:" + user.get.nickName)
    }
  }

  // test for extract user info
  it should "return Some(User) when all is correct" in {
    val qqService = new QQService("", "", "", None, None) //these params are not important in this test
    val rs: Option[User] = qqService.QQResponseExtractor.extractUserInfo(
      """{
      "ret":0,
      "msg":"",
      "nickname":"大魔头",
      "figureurl":"http://exmple.qq.com/qzapp/000000000000000000000000000F4262/50",
      "gender":"男"
}""")
    rs should not be None
    val user = rs.get
    user.nickName should be === "大魔头"
    user.figureUrl should be === "http://exmple.qq.com/qzapp/000000000000000000000000000F4262/50"
  }

  it should "extract openId when all is correct" in {
    val qqService = new QQService("", "", "", None, None) //these params are not important in this test
    val rs = qqService.QQResponseExtractor.extractOpenId(
      """callback( {"client_id":"YOUR_APPID","openid":"YOUR_OPENID"
} ); """)
    rs should not be None
    rs.get should be === UID("YOUR_OPENID")
  }
}