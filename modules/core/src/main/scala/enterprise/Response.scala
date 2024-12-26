package enterprise
import turbolift.!!
import enterprise.headers.ContentType


final case class Response(
  status: Status = Status.Ok,
  body: Body = Body.empty,
  headers: Headers = Headers.empty,
):
  def withStatus(status: Status): Response = copy(status = status)
  def withHeaders(headers: Headers): Response = copy(headers = headers)
  def withBody(body: Body): Response = copy(body = body)
  def withText(text: String): Response = withBody(Body(text), MediaType.TextPlain)
  def withJsonBody(body: Body): Response = withBody(body, MediaType.ApplicationJson)
  
  def withBody(body: Body, mediaType: MediaType): Response =
    copy(
      body = body,
      headers = headers.put(ContentType(mediaType)),
    )

  def modBody(f: Body => Body): Response = withBody(f(body))
  def modHeaders(f: Headers => Headers): Response = withHeaders(f(headers))

  def putHeader(h: Header): Response = modHeaders(_.put(h))
  def addHeader(h: Header): Response = modHeaders(_.add(h))

  def raise: Nothing !! ResponseError = ResponseError.raise(this)


object Response:
  def apply(body: Body, mediaType: MediaType): Response = 
    Response(
      status = Status.Ok,
      body = body,
      headers = Headers(ContentType(mediaType))
    )

  def text(text: String): Response = apply(Body(text), MediaType.TextPlain)
  def jsonBody(body: Body): Response = apply(body, MediaType.ApplicationJson)

  def badRequest(text: String): Response = apply(Status.BadRequest, Body(text))
