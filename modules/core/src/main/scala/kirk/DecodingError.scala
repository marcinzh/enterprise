package kirk
import turbolift.{!!, Handler}
import turbolift.Extensions._
import turbolift.effects.ErrorEffect
import enterprise.{Response, Status}


final class DecodingException(message: String) extends Exception(message)


type DecodingError = DecodingError.type

object DecodingError extends ErrorEffect[DecodingException, DecodingException]:
  def badRequest[U]: Handler[Const[Response[U]], Const[Response[U]], DecodingError, Any] =
    handlers.first
    .project[Response[U]]
    .mapK([_] => (ee: Either[DecodingException, Response[U]]) => ee match
      case Right(r) => r
      case Left(e) => Response(Status.BadRequest).withText(e.getMessage)
    )
