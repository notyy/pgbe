package pgbe.snippet

import scala.xml.{ NodeSeq, Text }
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.JE._
import net.liftweb.http.SHtml
import net.liftweb.http.SHtml._
import net.liftweb.common.Logger
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.jquery.JqJsCmds.{ Show, Hide }
import pgbe.model.CommentStack
import java.text.SimpleDateFormat
import pgbe.model.Comment
import java.util.Date
import scala.collection.mutable.SynchronizedMap
import scala.xml.UnprefixedAttribute
import scala.xml.Null

class Comments extends Logger {
  var id = 0
  val id2User = new scala.collection.mutable.HashMap[Int, String] with SynchronizedMap[Int, String]
  val id2Content = new scala.collection.mutable.HashMap[Int, String] with SynchronizedMap[Int, String]
  val id2Status = new scala.collection.mutable.HashMap[Int, Boolean] with SynchronizedMap[Int, Boolean]
  //  def incId: String = { id += 1; id.toString() }

  private def getPreCommentsView(eId: Int): NodeSeq = {
    val preCommentView: NodeSeq = Text("")
    if (CommentStack.cmap.contains(eId)) {
      val preComments = CommentStack.cmap(eId).foldLeft[NodeSeq](Text("")) {
        (xml, comment) =>
          xml ++ <span>{ comment.userName } 发表于 </span><span>{ comment.timeStr }</span><br/><span>{ comment.content }</span><p/>
      }
      <div class="well">{ preComments }</div>
    } else Text("")
  }

  private def createCommentLink(eId: Int): () => JsCmd = { () =>
    val editId = "edit_" + eId.toString()
    if (id2Status(eId)) { //hide edit form
      id2Status(eId) = false
      Hide(editId, 500 millis)
    } else { //show edit form and previous submited comment
      id2Status(eId) = true
      def createCommentBlock(eId: Int): JsCmd = {
        SetHtml(editId,
          getPreCommentsView(eId) ++
            ajaxForm(
              ajaxText("", { (userName: String) => //user name input
                id2User(eId) = userName
              }, "class" -> "span4", "placeholder" -> "您的名称") ++
                ajaxTextarea("", { (content: String) => //content input
                  id2Content(eId) = content
                }, "id" -> (editId), "class" -> "span7", "placeholder" -> "您的意见是什么？", "rows" -> "5") ++ <br/> ++
                ajaxSubmit("确认", { () => //submit button
                  val df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                  val comment = Comment(id2User(eId), df.format(new Date()), id2Content(eId))
                  if (CommentStack.cmap.contains(eId)) {
                    CommentStack.cmap(eId) = CommentStack.cmap(eId) :+ comment
                  } else CommentStack.cmap(eId) = List(comment)
                  createCommentBlock(eId) &
                    SetHtml(eId.toString(), Text(linkCaption))
                }, "class" -> "btn btn-primary", "type" -> "submit")) % new UnprefixedAttribute("class", "well", Null))
      }
      createCommentBlock(eId) &
        Show(editId, 500 millis)
    }
  }

  def linkCaption = if (CommentStack.cmap.contains(id)) CommentStack.cmap(id).length + "条评论" else "没有评论"

  def render = "p *+" #> { in: NodeSeq =>
    id += 1
    id2Status(id) = false
    SHtml.a(createCommentLink(id), Text(linkCaption), "id" -> id.toString(), "class" -> "btn btn-mini") ++ <div style="display:none" id={ "edit_" + id.toString() }/>
  }
}