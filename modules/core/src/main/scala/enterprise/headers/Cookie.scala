package enterprise.headers
import enterprise.HeaderKey.Companion
import enterprise.Header.Constructor
import enterprise.RequestCookie


final case class Cookie(cookies: List[RequestCookie]) extends Constructor[Cookie](Cookie):
  override def renderValue: String = cookies.iterator.map(_.render).mkString(";")


case object Cookie extends Companion[Cookie]:
  override def parseValue(text: String) =
    Cookie(text.split(';').iterator.flatMap(RequestCookie.tryParse).toList)
