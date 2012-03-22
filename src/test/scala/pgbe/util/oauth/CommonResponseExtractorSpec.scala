package pgbe.util.oauth
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import pgbe.util.oauth.model.User

class CommonResponseExtractorSpec extends FlatSpec with ShouldMatchers {
  object responseExtractor extends CommonResponseExtractor

  // test for extract token
  "ResponseExtractor" should "extract token when all is correct" in {
    val rs: Option[Token] = responseExtractor.extractToken(
      "access_token=C4F0730E395BCD0E58519CD65A485824&expires_in=7776000")
    rs.isEmpty should not be true
    rs.get.accessToken should be === "C4F0730E395BCD0E58519CD65A485824"
    rs.get.expire should be === 7776000
  }

  // test for extract auth code
  it should "extract the authorization code if code length longer than 6" in {
    responseExtractor.extractAuthCode("A6F90EF4B5******5F58801AC", "") should be === Some("A6F90EF4B5******5F58801AC")
  }

  it should "return None if the code is invalid" in {
    responseExtractor.extractAuthCode("100002", "code is wrong") should be === None
  }

  // test common filter
  it should "pass param on when given correct ret value" in {
    val resp = """{
      "ret":0,
      "msg":"",
      "nickname":"Peter",
      "figureurl":"http://exmple.qq.com/qzapp/000000000000000000000000000F4262/50",
      "gender":"男"
      }"""
    responseExtractor.filter(resp) should be === Some(resp)
  }

  it should "return None when given error ret value" in {
    val rs = responseExtractor.filter(
      """{
      "ret":2021,
      "msg":"请先登录"
     }""") should be === None
  }

  it should "return None when server response error code" in {
    responseExtractor.filter(
      """callback( {"error":100020,"error_description":"code is reused error"} );\n  """) should be === None
  }

}