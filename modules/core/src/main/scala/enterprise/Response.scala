package enterprise
import turbolift.!!
import enterprise.headers.ContentType


final case class Response[-U](
  status: Status = Status.Ok,
  body: Body[U] = Body.empty,
  headers: Headers = Headers.empty,
):
  def withStatus(status: Status): Response[U] = copy(status = status)
  def withHeaders(headers: Headers): Response[U] = copy(headers = headers)
  def withBody[V](body: Body[V]): Response[V] = copy(body = body)
  def withJsonBody[V](body: Body[V]): Response[V] = withBody(body, MediaType.ApplicationJson)
  def withText(text: String): Response[Any] = withBody(Body.fromText(text), MediaType.TextPlain)
  
  def withBody[V](body: Body[V], mediaType: MediaType): Response[V] =
    copy(
      body = body,
      headers = headers.put(ContentType(mediaType)),
    )

  def modBody[V](f: Body[U] => Body[V]): Response[V] = withBody(f(body))
  def modHeaders(f: Headers => Headers): Response[U] = withHeaders(f(headers))

  def putHeader(h: Header): Response[U] = modHeaders(_.put(h))
  def addHeader(h: Header): Response[U] = modHeaders(_.add(h))

  def getCookie(name: String): Option[ResponseCookie] = headers.getResponseCookie(name)
  def addCookie(cookie: ResponseCookie): Response[U] = modHeaders(_.addResponseCookie(cookie))

  // def raise(using ev: U =:= Any): Nothing !! ResponseError = ResponseError.raise(cast[Any])
  // def cast[V](using ev: U =:= V): Response[V] = asInstanceOf[Response[V]]




object Response:
  val Ok: Response[Any] = text("")

  def apply[U](body: Body[U], mediaType: MediaType): Response[U] = 
    Response(
      status = Status.Ok,
      body = body,
      headers = Headers(ContentType(mediaType))
    )

  def text(text: String): Response[Any] = apply(Body.fromText(text), MediaType.TextPlain)
  def jsonBody[U](body: Body[U]): Response[U] = apply(body, MediaType.ApplicationJson)

  def badRequest(text: String): Response[Any] = apply(Status.BadRequest, Body.fromText(text))
