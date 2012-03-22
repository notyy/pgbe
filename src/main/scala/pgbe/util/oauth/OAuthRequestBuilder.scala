package pgbe.util.oauth
import org.apache.http.message.BasicNameValuePair
import org.slf4j.LoggerFactory
import pgbe.util.LogUtil._
import org.apache.http.client.utils.URIUtils
import org.apache.http.client.utils.URLEncodedUtils

object OAuthRequestBuilder {
  val logger = LoggerFactory.getLogger(OAuthRequestBuilder.getClass())

  def build(rawReq: Request): Option[String] = {
    import scala.collection.JavaConversions._
    val params = for (param <- rawReq.params) yield (new BasicNameValuePair(param.name, param.value))
    val e = rawReq.endPoint
    try {
      Some(URIUtils.createURI(e.scheme, e.host, -1, e.path,
        URLEncodedUtils.format(params, "UTF-8"), null).toString())
    } catch {
      case e: Exception => { logger.error("error when building requestURI", e); None }
    }
  }
}