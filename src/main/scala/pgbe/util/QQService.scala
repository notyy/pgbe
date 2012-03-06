package pgbe.util
import org.scribe.oauth.OAuthService
import org.scribe.builder.ServiceBuilder
import org.scribe.model.SignatureType
import java.util.Scanner

object QQService {
  val scanner = new Scanner(System.in)

  def requestAuthUrl(apiKey: String, apiSecret: String, callBackURI: String) = {
    val service: OAuthService = (new ServiceBuilder().provider(new QQApi())).
      apiKey(apiKey).apiSecret(apiSecret).callback(callBackURI).
      signatureType(SignatureType.QueryString).build()
    service.getAuthorizationUrl(null)
  }

}