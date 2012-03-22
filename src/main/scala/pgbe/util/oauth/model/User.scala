package pgbe.util.oauth.model

case class User(
  var comeFrom: String,
  var nickName: String,
  var figureUrl: String,
  var openId: Option[String],
  var accessToken: Option[String])