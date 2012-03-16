package pgbe.util.oauth
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class ResponseExtractorSpec extends FlatSpec with ShouldMatchers {
  "ResponseExtractor" should "extract token when all is correct" in {
    val rs: Option[Token] = ResponseExtractor.extractToken(
      Some("access_token=C4F0730E395BCD0E58519CD65A485824&expires_in=7776000"))
    rs.isEmpty should not be true
    rs.get.accessToken should be === "C4F0730E395BCD0E58519CD65A485824"
    rs.get.expire should be === 7776000
  }

  it should "return None when given None parameter" in {
    ResponseExtractor.extractToken(None) should be === None
  }

  it should "return None when given error code from server" in {
    val rs: Option[Token] = ResponseExtractor.extractToken(
      Some("""callback( {"error":100020,"error_description":"code is reused error"} );\n  """))
    rs.isEmpty should be === true
  }

  it should "extract the authorization code if code length longer than 6" in {
    ResponseExtractor.extractAuthCode("A6F90EF4B5******5F58801AC", "") should be === Some("A6F90EF4B5******5F58801AC")
  }

  it should "return null if the code is invalid" in {
    ResponseExtractor.extractAuthCode("100002", "code is wrong").isEmpty should be === true
  }
}