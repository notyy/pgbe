package pgbe.snippet

import scala.xml.{ NodeSeq }
import net.liftweb.util.Helpers._
import net.liftweb.http.LiftScreen
import pgbe.model.Comment
import net.liftweb.http.S
import net.liftweb.http.js.jquery.JqJsCmds.Show
import net.liftweb.http.js.JsCmds.Alert
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.SetHtml
import scala.xml.Text
import scala.xml.Elem

class CommentScreen(val ppId: String, val onDone: () => JsCmd) extends LiftScreen {
  override def defaultToAjax_? = true
  override def finishButton: Elem = <input type="submit" class="btn btn-primary btn-large" value="确定"/>

  object comment extends ScreenVar(Comment.create)

  val f = comment.content
  addFields(() => comment.author)
  addFields(() => comment.content)

  def finish() {
    comment.ppId := ppId
    if (comment.save()) S.notice("感谢评论")
    else S.error("评论保存失败")
    //    comment.remove()
  }

  override def calcAjaxOnDone = onDone()
}