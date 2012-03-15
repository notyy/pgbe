package pgbe.util.oauth
import org.slf4j.LoggerFactory
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import pgbe.util.LogUtil._
import org.apache.http.HttpEntity

object Connector {
  val logger = LoggerFactory.getLogger(Connector.getClass())
  def send_!(reqURI: String, verb: Verb.Value): Option[String] = {
    logger.info("sending:" + reqURI + ",verb:" + verb)
    val sender = verb match {
      case Verb.GET => new HttpGet(reqURI)
      case Verb.POST => new HttpPost(reqURI)
    }
    var entity: HttpEntity = null
    try {
      val resp = EntityUtils.toString(new DefaultHttpClient().execute(sender).getEntity())
      if (resp == null || resp.isEmpty()) None else Some(resp)
    } catch {
      case e: Exception => logV(logger.error)("failed to communicate with oauth server,when sending:\n" + reqURI, None)
    } finally {
      if (entity != null) EntityUtils.consume(entity)
    }
  }
}