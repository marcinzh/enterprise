package enterprise


final case class Request(
  method: Method,
  path: String,
  headers: Headers,
  body: Body,
):
  override def toString = s"$method $path $body"


object Request:
  export RequestEffect.{ask, asks, handler}
