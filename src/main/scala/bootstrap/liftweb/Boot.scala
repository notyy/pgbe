package bootstrap.liftweb

import net.liftweb._
import sitemap._
import Loc._
import util.{ NamedPF }
import http._
import net.liftweb.common.Full
import net.liftweb.http.{ LiftRules, StrictXHTML1_0Validator }
import net.liftweb.db.StandardDBVendor
import net.liftweb.util.Props
import net.liftweb.mapper.DB
import net.liftweb.db.DefaultConnectionIdentifier
import net.liftweb.common.Logger
import net.liftweb.mapper.Schemifier
import net.liftweb.util.Helpers._
import pgbe.model.Comment
import pgbe.util.Config

class Boot extends Logger {
  def boot {
    info("booting in mode:" + Props.mode)
    // where to search snippet
    LiftRules.addToPackages("pgbe")
    //LiftRules.xhtmlValidator = Full(StrictXHTML1_0Validator)

    // build sitemap
    val entries = List(
      //TODO: use i18n
      Menu("Home", "首页") / "index" >> LocGroup("read"),
      Menu("ArticleDetail", "阅读全文") / "articleDetail" >> Hidden
        >> Unless(
          () => S.param("id").isEmpty,
          () => RedirectResponse("index")),
      Menu("RSS", "RSS订阅") / "rss" >> LocGroup("read"),
      Menu("ArticleList", "文章列表") / "articleList" >> LocGroup("read"),
      //---------------------------------------------------------------
      Menu("register", "注册") / "register" >> LocGroup("user"),
      Menu("login", "登录") / "userLogin" >> LocGroup("user"),
      Menu("read", "阅读") / "upload" / ** >> LocGroup("user") >> Hidden,
      Menu("imgs", "图片") / "imgs" / ** >> LocGroup("static") >> Hidden,
      //---------------------------------------------------------------
      Menu("Admin", "管理") / "admin" >> LocGroup("admin") submenus (
        Menu("ArticleListAdmin", "文章列表") / "admin" / "articleList",
        Menu("AddArticleAdmin", "添加文章") / "admin" / "addArticle"))

    LiftRules.uriNotFound.prepend(NamedPF("404handler") {
      case (req, failure) => NotFoundAsTemplate(
        ParsePath(List("exceptions", "404"), "html", false, false))
    })

    LiftRules.setSiteMap(SiteMap(entries: _*))

    // set character encoding
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    //connecting database
    if (!DB.jndiJdbcConnAvailable_?) {
      DB.defineConnectionManager(
        DefaultConnectionIdentifier, DBVendor)
      LiftRules.unloadHooks.append(() =>
        DBVendor.closeAllConnections_!())
    }

    //schemify
    if (Props.devMode)
      Schemifier.schemify(true, Schemifier.infoF _,
        Comment)

    //set some config values
    Config.qqApiKey = Props.get("QQ.apiKey")
    Config.qqApiScret = Props.get("QQ.apiSecret")
    Config.hostDomain = Props.get("host.domain")

    //some styling
    LiftRules.noticesAutoFadeOut.default.set(
      (notices: NoticeType.Value) => Full(2 seconds, 2 seconds))
  }

  object DBVendor extends StandardDBVendor(
    Props.get("db.class").openOr("org.h2.Driver"),
    Props.get("db.url").openOr("jdbc:h2:~/database/pgbe"),
    Props.get("db.user"), Props.get("db.pass"))

}