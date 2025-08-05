package enterprise
import turbolift.effects.IO


type RequestIO = Request[IO]

final case class Request[-U](
  method: Method,
  path: String,
  headers: Headers,
  body: Body[U],
):
  override def toString = s"$method $path"

  def withHeaders(headers: Headers): Request[U] = copy(headers = headers)
  def modHeaders(f: Headers => Headers): Request[U] = withHeaders(f(headers))

  def getCookie(name: String): Option[RequestCookie] = headers.getRequestCookie(name)
  def addCookie(cookie: RequestCookie): Request[U] = modHeaders(_.addRequestCookie(cookie))

  def cookies: Map[String, RequestCookie] = headers.cookies

object Request:
  export RequestEffect.{ask, asks, asksEff}
  export RequestEffect.handlers.{default => handler}
