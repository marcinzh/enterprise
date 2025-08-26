package enterprise


final case class RequestCookie(name: String, content: String):
  def render: String = s"$name=$content"


object RequestCookie:
  def tryParse(text: String): Option[RequestCookie] =
    text match
      case RX(name, content) => Some(RequestCookie(name, content))
      case _ => None

  private val RX = """^\s*([^=]+)\s*=\s*(.*)\s*$""".r
