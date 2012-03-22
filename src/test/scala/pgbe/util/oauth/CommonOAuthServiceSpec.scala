package pgbe.util.oauth
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.slf4j.LoggerFactory
import org.scalatest.fixture.FixtureFlatSpec
import java.net.URLEncoder

class CommonOAuthServiceSpec extends FixtureFlatSpec with ShouldMatchers {

  val logger = LoggerFactory.getLogger(this.getClass())

  type FixtureParam = OAuthServiceMockImpl

  def withFixture(test: OneArgTest) {
    test(new (OAuthServiceMockImpl))
  }

  "OAuth2 spec" should "Step1:generate a url so that user can jump to oauth2 provider and grant us authorization code" in
    { oauthService =>
      val rs = "https://provider.sample/authorize?client_id=clientId_01234&redirect_uri=http%3A%2F%2Fmysite.sample%2Fcallback&response_type=code&state=from_some_provider"
      oauthService.makeAuthUrl should be === Some(rs)
    }

  it should "Step2:request an access token when given verify code from previous step of oauth2  " in { oauthService =>
    logger.info("skip this integration test for step2")
  }

  it should "Step3:request resource using access token" in { OauthService =>
    logger.info("skip this integration test for step3")
  }
}