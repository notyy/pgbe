package bootstrap.liftweb

import net.liftweb._
import sitemap._
import Loc._
import util.{ NamedPF }
import http._
import net.liftweb.common.Full
import net.liftweb.http.{ LiftRules, StrictXHTML1_0Validator }

class Boot {
  def boot {
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
      Menu("login", "登录") / "login" >> LocGroup("user"),
      Menu("read", "阅读") / "upload" / ** >> LocGroup("user") >> Hidden,
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
  }
}