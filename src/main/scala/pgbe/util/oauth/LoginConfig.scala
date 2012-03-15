package pgbe.util.oauth

abstract trait LoginConfig {
  val AUTHORIZE_URL: EndPoint
  val ACCESS_TOKEN_RUL: EndPoint
  val GET_OPENID_URL: EndPoint
}