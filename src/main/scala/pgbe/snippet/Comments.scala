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
import java.text.SimpleDateFormat
import pgbe.model.Comment
import java.util.Date
import scala.collection.mutable.SynchronizedMap
import scala.xml.UnprefixedAttribute
import scala.xml.Null
import net.liftweb.http.S
import net.liftweb.mapper.By
import net.liftweb.mapper.OrderBy
import net.liftweb.mapper.Ascending
import net.liftweb.http.SessionVar
import net.liftweb.common.Box
import pgbe.model.User
import net.liftweb.common.Empty
import pgbe.util.QQService
import java.net.URI
import pgbe.util.Config
import net.liftweb.common.Full

object userVar extends SessionVar[Box[User]](Empty)

class Comments extends Logger {
  var id = 0
  val id2Status = new scala.collection.mutable.HashMap[Int, Boolean] with SynchronizedMap[Int, Boolean]
  val pageUrl = S.uri
  val code = S.attr("code", _.toString)
  val state = S.attr("state", _.toString)

  def ppId(id: Int) = pageUrl + "_" + id

  def render = "p *+" #> { in: NodeSeq =>
    (code, state) match {
      case (Full(sCode), _) => {
        info("requesting accessToken-----------:\n")
        val token = QQService.requestAccessToken(sCode)
        info("token received, that is-----------:\n" + token)
        val openId = QQService.requestOpenId(token)
        info("now, we got openId, -------------:\n" + openId)
        val userInfo = QQService.requestUserInfo(token, openId)
        renderP
      }
      case _ => info("No code para, normal rendering"); renderP //暂时忽略state参数
    }
  }

  def renderP = {
    id += 1
    id2Status(id) = false
    SHtml.a(createCommentLink(id), Text(linkCaption(id)), "id" -> ppId(id), "class" -> "btn-mini") ++
      <div style="display:none" id={ "edit_" + id.toString() }/>
  }

  private def linkCaption(eId: Int) = {
    val commentsOnPLength = Comment.findAll(By(Comment.ppId, ppId(eId)), OrderBy(Comment.createdAt, Ascending)).length
    if (commentsOnPLength > 0) (commentsOnPLength + "条评论") else "没有评论"
  }

  private def createCommentLink(eId: Int): () => JsCmd = { () =>
    val editId = "edit_" + eId.toString()
    if (id2Status(eId)) { //hide edit form
      id2Status(eId) = false
      Hide(editId, 500 millis)
    } else { //show edit form and previous submited comment
      id2Status(eId) = true
      createCommentBlock(eId, editId)() &
        Show(editId, 500 millis)
    }
  }

  def createCommentBlock(eId: Int, editId: String): () => JsCmd = { () =>
    SetHtml(editId,
      getOldCommentsView(eId) ++
        (if (userVar.isEmpty) loginBlock(eId) else new CommentScreen(eId.toString(), createCommentBlock(eId, editId)).toForm)) &
      SetHtml(ppId(eId).toString(), Text(linkCaption(eId)))
  }

  def loginBlock(eId: Int): NodeSeq = {
    val hostDomain = Config.hostDomain.openOr("")
    val callBack = hostDomain + pageUrl + "?state=test" // + ppId(eId)
    info("requesting authurl-------callback=\n" + callBack)
    val requestAuthUrl = new URI(QQService.requestAuthUrl(callBack))
    <div><h4 class="alert alert-info">您必须登录后才能评论，本小站无力维护密码安全，请点击下面图标使用大公司的登录服务</h4></div>
    <div><span id="qqLoginBtn"></span><a href={ requestAuthUrl.toString() }><img src="../imgs/QQ_Connect_logo_7.png" alt="QQ登录 "/></a></div>
  }

  private def getOldCommentsView(eId: => Int): NodeSeq = {
    val commentsOnP = Comment.findAll(By(Comment.ppId, eId.toString()), OrderBy(Comment.createdAt, Ascending))
    val preCommentView: NodeSeq = Text("")
    val df = new SimpleDateFormat("yyyy年 MM月 dd日 HH时 mm分 ss秒")
    if (commentsOnP.isEmpty) Text("")
    else {
      val preComments = commentsOnP.foldLeft[NodeSeq](Text("")) {
        (xml, comment) =>
          xml ++ <span>{ comment.author } 发表于 </span><span>{ df.format(comment.createdAt.is) }</span><br/><span>{ comment.content }</span><p/>
      }
      <div id={ "old_" + eId } class="well">{ preComments }</div>
    }
  }

}