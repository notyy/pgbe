package pgbe.util
import net.liftweb.common.Box
import net.liftweb.common.Empty
import net.liftweb.util.Props

object Config {
  //set some config values
  val PROP_QQ_APIKEY = "QQ.apiKey"
  val PROP_QQ_APISECRET = "QQ.apiSecret"
  val PROP_HOSTDOMAIN = "host.domain"
  val qqApiKeyVar = Props.get(PROP_QQ_APIKEY).openOr("")
  val qqApiScretVar = Props.get(PROP_QQ_APISECRET).openOr("")
  val hostDomainVar = Props.get(PROP_HOSTDOMAIN).openOr("")
  val qqReady_? = {
    if (Props.get(PROP_QQ_APIKEY).isEmpty || Props.get(PROP_QQ_APISECRET).isEmpty) false
    else true
  }
}