package pgbe.model

case class User(
  val comeFrom: String,
  val nickName: String,
  val figureUrl: String,
  val openId: Option[String],
  val accessToken: Option[String])