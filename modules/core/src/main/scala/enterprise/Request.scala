package enterprise
import turbolift.!!
import turbolift.Extensions._
import turbolift.effects.Reader
import enterprise.model._


final case class Request(
  method: Method,
  path: String,
  headers: Headers,
  body: Body,
):
  override def toString = s"$method $path $body"


object Request:
  case object Fx extends Reader[Request]
  type Fx = Fx.type
  export Fx.{ask, asks}
