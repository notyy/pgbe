package pgbe.util
import org.scribe.builder.api.DefaultApi20
import org.scribe.model.OAuthConfig
import org.scribe.utils.Preconditions
import org.scribe.utils.OAuthEncoder

class QQApi extends DefaultApi20 {
  private val AUTHORIZE_URL = "https://graph.qq.com/oauth2.0/authorize?client_id=%s&redirect_uri=%s&response_type=code" //&state=%s
  private val SCOPED_AUTHORIZE_URL = AUTHORIZE_URL + "&scope=%s"

  def getAccessTokenEndpoint(): String = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code"

  def getAuthorizationUrl(config: OAuthConfig): String = {
    Preconditions.checkValidUrl(config.getCallback(), "Must provide a valid url as callback. QQ requires it");

    // Append scope if present
    if (config.hasScope()) {
      return String.format(SCOPED_AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()), OAuthEncoder.encode(config.getScope()));
    } else {
      return String.format(AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
    }
  }

}