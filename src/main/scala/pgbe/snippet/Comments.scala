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
import net.liftweb.common.Empty
import pgbe.util.oauth._
import java.net.URI
import pgbe.util.Config
import net.liftweb.common.Full
import net.liftweb.json._
import net.liftweb.http.provider.HTTPCookie
import Config._
import pgbe.util.oauth.model.User
import pgbe.util.oauth.lift.OAuthRenew
import org.slf4j.LoggerFactory

class Comments {
  val logger = LoggerFactory.getLogger(this.getClass())
  private var id = 0
  private val id2Status = new scala.collection.mutable.HashMap[Int, Boolean] with SynchronizedMap[Int, Boolean]
  private val pageUrl = S.uri
  private val oAuthRenew = new OAuthRenew
  private val userVar = oAuthRenew.identifyUser_!()

  def ppId(id: Int) = pageUrl + "_" + id

  def render = "p *+" #> { in: NodeSeq =>
    id += 1
    id2Status(id) = false
    SHtml.a(createCommentLink_!(id), Text(linkCaption_!(id)), "id" -> ppId(id), "class" -> "btn-mini") ++
      <div style="display:none" id={ "edit_" + id.toString() }/>
  }

  private def linkCaption_!(eId: Int) = {
    val commentsOnPLength = Comment.findAll(By(Comment.ppId, ppId(eId)), OrderBy(Comment.createdAt, Ascending)).length
    if (commentsOnPLength > 0) (commentsOnPLength + "条评论") else "没有评论"
  }

  private def createCommentLink_!(eId: Int): () => JsCmd = { () =>
    val editId = "edit_" + eId.toString()
    if (id2Status(eId)) { //hide edit form
      id2Status(eId) = false
      Hide(editId, 500 millis)
    } else { //show edit form and previous submited comment
      id2Status(eId) = true
      createCommentBlock_!(eId, editId)() &
        Show(editId, 500 millis)
    }
  }

  def createCommentBlock_!(eId: Int, editId: String): () => JsCmd = { () =>
    val jsCmd = SetHtml(editId,
      getOldCommentsView(eId)
        ++
        (if (userVar.isEmpty) loginBlock(eId)
        else {
          val nickName = userVar.map(_.nickName).openOr("")
          val comeFrom = userVar.map(_.comeFrom).openOr("")
          val figureUrl = userVar.map(_.figureUrl).openOr("")
          val commentForm = <span>欢迎评论，来自{ comeFrom }的{ nickName }</span> ++ new CommentScreen(ppId(eId), nickName, comeFrom, figureUrl, createCommentBlock_!(eId, editId)).toForm
          commentForm
        })) & {
        val changeLinkJS = SetHtml(ppId(eId), Text(linkCaption_!(eId)))
        changeLinkJS
      }
    jsCmd
  }

  def loginBlock(eId: Int): NodeSeq = {
    val requestAuthUrl = oAuthRenew.makeAuthUrl
    val loginBlock: NodeSeq = {
      <div><h4 class="alert alert-info">您必须登录后才能评论，本小站不敢劳您注册，请点击下面图标使用大公司的登录服务</h4></div> ++
        <div><span id="qqLoginBtn"></span><a href={ requestAuthUrl.toString() }><img src="../imgs/QQ_Connect_logo_7.png" alt="QQ登录 "/></a></div>
    }
    loginBlock
  }

  private def getOldCommentsView(eId: => Int): NodeSeq = {
    val commentsOnP = Comment.findAll(By(Comment.ppId, ppId(eId)), OrderBy(Comment.createdAt, Ascending))
    val preCommentView: NodeSeq = Text("")
    val df = new SimpleDateFormat("yyyy年 MM月 dd日 HH时 mm分 ss秒")
    val oldComments = {
      if (commentsOnP.isEmpty) Text("")
      else {
        val preComments = commentsOnP.foldLeft[NodeSeq](Text("")) {
          (xml, comment) =>
            xml ++ <span><img src={ isOr(comment.authorFigureUrl, "") } alt=""></img> 来自{ isOr(comment.authorFrom, "") }的网友 <strong>{ isOr(comment.author, "") }</strong> 发表于 </span><span>{ df.format(comment.createdAt.is) }</span><br/><span>{ comment.content }</span><p/>
        }
        <div id={ "old_" + eId } class="well">{ preComments }</div>
      }
    }
    oldComments
  }

  def isOr(s: String, default: String) = if (s == null || s.isEmpty()) default else s

}