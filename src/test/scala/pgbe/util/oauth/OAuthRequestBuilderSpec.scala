package pgbe.util.oauth
import org.scalatest.fixture.FixtureFlatSpec
import org.scalatest.matchers.ShouldMatchers

class OAuthRequestBuilderSpec extends FixtureFlatSpec with ShouldMatchers {

  type FixtureParam = OAuthServiceMockImpl

  def withFixture(test: OneArgTest) {
    test(new (OAuthServiceMockImpl))
  }

  "OAuthRequestBuilder" should "build a valid http uri based on the given rawrequest" in { oAuthService =>
    val req = new Request(oAuthService.endPointForAuthorizationCode,
      Param("client_id", oAuthService.clientId),
      Param("redirect_uri", oAuthService.redirectUri.get),
      Param("response_type", "code"),
      Param("state", oAuthService.state))
    OAuthRequestBuilder.build(req) should be ===
      "https://provider.sample/authorize?client_id=clientId_01234&" +
      "redirect_uri=http%3A%2F%2Fmysite.sample%2Fcallback&response_type=code&state=from_some_provider"
  }
}