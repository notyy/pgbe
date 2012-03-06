package pgbe.util
import net.liftweb.common.Box
import net.liftweb.common.Empty

object Config {
  var qqApiKey: Box[String] = Empty
  var qqApiScret: Box[String] = Empty
  var hostDomain: Box[String] = Empty
}