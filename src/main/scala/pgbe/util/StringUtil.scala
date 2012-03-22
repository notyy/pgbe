package pgbe.util

object StringUtil {

  /**
   * compact a multiline string to one line, and trim out each lines blank space
   * @param s
   * @return
   */
  def compact(s: String): String = {
    s.lines.map((s1) => s1.trim()).mkString("")
  }
}