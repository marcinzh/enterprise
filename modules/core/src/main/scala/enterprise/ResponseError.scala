package enterprise
import turbolift.{!!, Handler}
import turbolift.Extensions._
import turbolift.effects.ErrorEffect


trait ResponseErrorEffect[U] extends ErrorEffect[Response[U]]:
  def handler: ThisHandler[Const[Response[U]], Const[Response[U]], Any] =
    handlers.first
    .project[Response[U]]
    .mapK([_] => (e: Either[Response[U], Response[U]]) => e.merge)

  def raiseBadRequest(text: String) = raise(Response.badRequest(text))


//// predefined instance of ResponseErrorEffect
case object ResponseError extends ResponseErrorEffect[Any]

type ResponseError = ResponseError.type
