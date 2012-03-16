package pgbe.util.oauth
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.slf4j.LoggerFactory
import java.util.Properties

/**
 * this is not a real automated unit test,instead, it's used to really test send and receive
 * messages from OAuth2.0 provider
 * @author yy
 *
 */
class ConnectorSpec extends FlatSpec with ShouldMatchers {
  val logger = LoggerFactory.getLogger(this.getClass())
  "Connector" should "Http Get or Post to real server and get result as a string" in {
    val shouldRun_? = false //so that we can control whether this test should be run or not
    if (shouldRun_?) {
      logger.debug("loading test properties")
      val properties = new Properties
      properties.load(this.getClass().getResourceAsStream("/test.properties"))
      logger.debug("find properties:")
      properties.list(Console.out)
      val authUrl = OAuthRequestBuilder.build(Request(new EndPoint("https", "graph.qq.com", "/oauth2.0/token", Verb.GET),
        Param("client_id", properties.getProperty("apiKey")),
        Param("client_secret", properties.getProperty("apiSecret")),
        Param("code", "287CDC75605646A8DA5023ABA051CD4E"), //replace this with true code 
        Param("grant_type", "authorization_code"),
        Param("redirect_uri", "http://kaopua.com"),
        Param("state", "fromQQ")))
      logger.info("calling url:\n" + authUrl)
      logger.info("request sent....and got response:\n" + Connector.send_!(authUrl, Verb.GET))
    } else logger.info("connector test is skipped!")
  }
}