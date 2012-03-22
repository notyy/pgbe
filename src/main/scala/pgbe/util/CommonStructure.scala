package pgbe.util
import org.slf4j.Logger
import pgbe.util.LogUtil._

object CommonStructure {

  class Do[A, B](val desc: String, val f: (A => Option[B]))(implicit logger: Logger) {
    def apply(param: A) = {
      logger.info(desc + " with param:" + param);
      logV(logger.info)(desc + " with param:" + param + " produce result ", f(param))
    }
  }

  object Do {
    def apply[A, B](desc: String, f: (A => Option[B]))(implicit logger: Logger) =
      new Do(desc, f)(logger).apply _
  }

  class Monadic[A, B](val f1: (A => Option[B])) {
    def then[C]: (B => Option[C]) => (A => Option[C]) = thenDo(f1) _
  }

  def thenDo[A, B, C](fa2ob: (A => Option[B]))(fb2oc: (B => Option[C])): (A => Option[C]) = { a =>
    fa2ob(a) match {
      case None => None
      case Some(b) => fb2oc(b)
    }
  }

  implicit def fToM[A, B](f: (A => Option[B])) = new Monadic(f)

  //  def withValueChecked[A, B](logger: Logger, param: Option[A], meaning: String)(f: A => Option[B]): Option[B] = {
  //    logger.info("checking... " + meaning)
  //    if (param.isEmpty) logV(logger.error)("can't process empty " + meaning, None)
  //    else {
  //      logger.info("processing " + meaning + ":  " + param)
  //      val rs = f(param.get)
  //      if (rs.isEmpty) logV(logger.error)("result is None when processing " + meaning + ": " + param, rs)
  //      else logV(logger.info)("processing of " + meaning + ": " + param + " OK,result is", rs)
  //    }
  //  }

  //  def withValueFiltered[A, B](logger: Logger, param: Option[A], meaning: String, filter: A => Option[A])(f: A => Option[B]): Option[B] = {
  //    logger.info("checking... " + meaning)
  //    if (param.isEmpty) logV(logger.error)("can't process empty " + meaning, None)
  //    else {
  //      logger.info("filtering " + meaning + ": " + param)
  //      val filtered = filter(param.get)
  //      if (filtered.isEmpty) logV(logger.error)(meaning + " doesn't pass filtering:" + param, None)
  //      else {
  //        logger.info("processing " + meaning + ": " + param)
  //        val rs = f(param.get)
  //        if (rs.isEmpty) logV(logger.error)("result is None when processing " + meaning + ": " + param, rs)
  //        else logV(logger.info)("processing of " + meaning + ": " + param + " OK,result is", rs)
  //      }
  //    }
  //  }
}
