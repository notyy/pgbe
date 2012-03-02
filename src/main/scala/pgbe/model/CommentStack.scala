package pgbe.model
import scala.collection.mutable.SynchronizedMap

case class Comment(userName: String, timeStr: String, content: String)

object CommentStack {
  val cmap = new scala.collection.mutable.HashMap[Int, List[Comment]] with SynchronizedMap[Int, List[Comment]]
}