package bootstrap.liftweb

import net.liftweb._
import http.{LiftRules, NotFoundAsTemplate, ParsePath}
import sitemap.{SiteMap, Menu, Loc}
import Loc._
import util.{ NamedPF }
import http._

class Boot {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("pgbe")

    // build sitemap
    val entries = List(
        //TODO: use i18n
        Menu("Home") / "index",
        Menu("Article Detail") / "articleDetail" >> Hidden
          >> Unless(
              () => S.param("id").isEmpty,
              () => RedirectResponse("index")),
        Menu("Article List") / "articleList"
        ) 
    
    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (req,failure) => NotFoundAsTemplate(
        ParsePath(List("exceptions","404"),"html",false,false))
    })
    
    LiftRules.setSiteMap(SiteMap(entries:_*))
    
    // set character encoding
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
  }
}