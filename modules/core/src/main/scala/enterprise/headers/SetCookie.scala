package enterprise.headers
import enterprise.HeaderKey.Companion
import enterprise.Header.Constructor
import enterprise.ResponseCookie


final case class SetCookie(cookie: ResponseCookie) extends Constructor[SetCookie](SetCookie):
  override def renderValue: String = cookie.render


case object SetCookie extends Companion[SetCookie]:
  override def parseValue(text: String) = ??? //@#@TODO
