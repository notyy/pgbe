package pgbe.util.oauth
import pgbe.util.oauth.model.User
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import pgbe.util.LogUtil._
import org.apache.http.message.BasicNameValuePair
import java.net.URI
import org.apache.http.client.utils.URIUtils
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.client.methods.HttpGet

object Verb extends Enumeration {
  val GET, PUT, POST, DELETE = Value
}

case class EndPoint(val scheme: String, val host: String, val path: String, val verb: Verb.Value)
case class Param(name: String, value: String)
case class Request(val endPoint: EndPoint, params: Param*)
case class Token(val accessToken: String, val expire: Long)

abstract trait OAuthService {
  val reqBuilder = OAuthRequestBuilder
  val connector = Connector

  val responseExtractor = ResponseExtractor
}