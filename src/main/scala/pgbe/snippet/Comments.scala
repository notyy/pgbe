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
import pgbe.util.oauth._
import java.net.URI
import pgbe.util.Config
import net.liftweb.common.Full
import net.liftweb.json._
import net.liftweb.http.provider.HTTPCookie
import org.scribe.model.Token

object userVar extends SessionVar[Box[User]](Empty)

class Comments extends Logger {
  val QQ_OPENID = "kaopua.qq.openId"
  val QQ_TOKEN = "kaopua.qq.token"
  var id = 0
  val id2Status = new scala.collection.mutable.HashMap[Int, Boolean] with SynchronizedMap[Int, Boolean]
  val pageUrl = S.uri
  val code = S.param("code")
  val state = S.param("state")
  val hostDomain = Config.hostDomain.openOr("")
  val callBack = hostDomain + pageUrl + "?state=test" // + ppId(eId)
  val qqService = QQService.initService(callBack)
  val openIdCookie = S.findCookie(QQ_OPENID)
  val tokenCookie = S.findCookie(QQ_TOKEN)

  { //初始化用户资料变量
    (userVar.is, openIdCookie, tokenCookie, code, state) match {
      case (Full(user), _, _, _, _) => ()
      case (Empty, Full(openId), Full(token), _, _) => {
        renewQQUserInfo(token.value.openOr(""), openId.value.openOr(""))
      }
      case (Empty, Empty, Empty, Full(sCode), _) => {
        QQService.initService(callBack)
        info("requesting accessToken-----------:\n")
        val token = QQService.requestAccessToken(qqService, sCode)
        if (token.isEmpty) {
          warn("failed to get token")
        } else {
          info("token received, that is-----------:\n" + token)
          val callback = QQService.requestOpenId(qqService, token.openTheBox)
          info("now, we got callback of openId, -------------:\n" + callback)
          val t1 = callback.drop(callback.indexOf("{"))
          val t2 = t1.take(t1.indexOf("}") + 1)
          val openId = (parse(t2) \ "openid").toString()
          info("extracted openId is:\n" + openId)
          renewQQUserInfo(token.openTheBox.getToken(), openId)
        }
      }
      case _ => info("No code para, normal rendering") //暂时忽略state参数
    }
  }

  def ppId(id: Int) = pageUrl + "_" + id

  def render = "p *+" #> { in: NodeSeq =>
    id += 1
    id2Status(id) = false
    SHtml.a(createCommentLink(id), Text(linkCaption(id)), "id" -> ppId(id), "class" -> "btn-mini") ++
      <div style="display:none" id={ "edit_" + id.toString() }/>
  }

  def renewQQUserInfo(token: String, openId: String) = {
    QQService.requestUserInfo(qqService, new Token(token, ""), openId, Config.qqApiKey.openOr("")) match {
      case Some(qqUserInfo) => {
        val user = User("QQ", qqUserInfo.nickName, qqUserInfo.figureurl, Some(openId), Some(token))
        userVar(Box !! (user))
        setCookie(openId, token)
      }
      case None => ()
    }
  }

  def setCookie(openId: String, token: String) = {
    val openIdCookie = HTTPCookie(QQ_OPENID, openId)
    openIdCookie.setDomain(Config.hostDomain.openOr(""))
    openIdCookie.setMaxAge(3600 * 24 * 7) //保留cookie一周
    val tokenCookie = HTTPCookie(QQ_TOKEN, token)
    openIdCookie.setDomain(Config.hostDomain.openOr(""))
    openIdCookie.setMaxAge(3600 * 24 * 7) //保留cookie一周
    S.addCookie(openIdCookie)
    S.addCookie(tokenCookie)
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
    info("createCommentBlock(eId=" + eId + ",editId=" + editId)

    val jsCmd = SetHtml(editId,
      getOldCommentsView(eId)
        ++
        (if (userVar.isEmpty) loginBlock(eId)
        else {
          info("creating comment form")
          val nickName = userVar.map(_.nickName).openOr("")
          val comeFrom = userVar.map(_.comeFrom).openOr("")
          val figureUrl = userVar.map(_.figureUrl).openOr("")
          val commentForm = <span></span> ++ new CommentScreen(ppId(eId), nickName, comeFrom, figureUrl, createCommentBlock(eId, editId)).toForm
          info("comment form created:\n" + commentForm)
          commentForm
        })) & {
        info("change link to show comment counts")
        val changeLinkJS = SetHtml(ppId(eId).toString(), Text(linkCaption(eId)))
        info("link counts shown")
        changeLinkJS
      }
    info("commentBlock created\n" + jsCmd)
    jsCmd
  }

  def loginBlock(eId: Int): NodeSeq = {
    info("requesting authurl-------callback=\n" + callBack)
    val requestAuthUrl = new URI(QQService.requestAuthUrl(qqService))
    info("authurl is ------------------=\n" + requestAuthUrl)
    val loginBlock: NodeSeq = {
      <div><h4 class="alert alert-info">您必须登录后才能评论，本小站无力维护密码安全，请点击下面图标使用大公司的登录服务</h4></div> ++
        <div><span id="qqLoginBtn"></span><a href={ requestAuthUrl.toString() }><img src="../imgs/QQ_Connect_logo_7.png" alt="QQ登录 "/></a></div>
    }
    info("loginBlock created\n" + loginBlock)
    loginBlock
  }

  private def getOldCommentsView(eId: => Int): NodeSeq = {
    info("getOldCommentsView:eId=" + eId)
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
    info("old comments retrieved\n" + oldComments)
    oldComments
  }

  def isOr(s: String, default: String) = if (s == null || s.isEmpty()) default else s

}