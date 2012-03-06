package pgbe.model

import net.liftweb.mapper._

class Comment extends LongKeyedMapper[Comment] with CreatedTrait with IdPK {
  def getSingleton = Comment

  object ppId extends MappedString(this, 50) { //page and <p> markup together as key for search
    override def dbIndexed_? = true
    override def dbNotNull_? = true
  }

  object author extends MappedString(this, 100) {
    override def dbNotNull_? = true
    override def displayName = "用户名"
    override def formElemAttrs = List("class" -> "span4", "placeholder" -> "您的名称")

    override def validations =
      valMinLen(3, "用户名最少3个字符") _ ::
        valMaxLen(20, "用户名最多20个字符") _ :: Nil
  }

  object content extends MappedTextarea(this, 255) {
    override def dbNotNull_? = true
    override def displayName = "内容"
    override def formElemAttrs = List("class" -> "span7", "placeholder" -> "您的意见是什么？", "rows" -> "5")

    override def validations =
      valMinLen(1, "内容不可以为空") _ ::
        valMaxLen(255, "内容最多个字符") _ :: Nil
  }
}

object Comment extends Comment with LongKeyedMetaMapper[Comment]