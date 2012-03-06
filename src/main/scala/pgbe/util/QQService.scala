package pgbe.util
import org.scribe.oauth.OAuthService
import org.scribe.builder.ServiceBuilder
import org.scribe.model.SignatureType
import java.util.Scanner

object QQService {
  val apiKey = "100250924" //apikey=apiId和apiSecret=key，需要向腾讯申请
  val apiSecret = "f4573000c321957d73b1126dd93992f6"
  val scanner = new Scanner(System.in)

  def requestAuthUrl(callBackURI: String) = {
    val service: OAuthService = (new ServiceBuilder().provider(new QQApi())).
      apiKey(apiKey).apiSecret(apiSecret).callback(callBackURI).
      signatureType(SignatureType.QueryString).build()
    service.getAuthorizationUrl(null)
  }

}