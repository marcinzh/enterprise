package enterprise
import turbolift.{!!, Handler}
import turbolift.Extensions._
import turbolift.effects.ErrorEffect


type ResponseError = ResponseError.type

object ResponseError extends ErrorEffect[Response, Response]:
  val handler: Handler[Const[Response], Const[Response], ResponseError, Any] =
    handlers.first
    .project[Response]
    .mapK([_] => (e: Either[Response, Response]) => e.merge)

  def raiseBadRequest(text: String) = raise(Response.badRequest(text))
