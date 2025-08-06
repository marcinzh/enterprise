package enterprise
import turbolift.{!!, Handler}
import turbolift.Extensions._
import turbolift.effects.PolyErrorEffect


type ResponseError = ResponseError.type

case object ResponseError extends PolyErrorEffect:
  def handler[U]: Handler[Const[Response[U]], Const[Response[U]], @@[Response[U]], Any] =
    handlers.default
    .project[Response[U]]
    .mapK([_] => (e: Either[Response[U], Response[U]]) => e.merge)

  def raiseBadRequest(text: String): Nothing !! @@[Response[Any]] = raise(Response.badRequest(text))
