package kirk
import turbolift.{!!, Handler}
import turbolift.Extensions._
import turbolift.effects.ErrorEffect
import enterprise.{Response, Status}


final class DecodingException(message: String) extends Exception(message)


type DecodingError = DecodingError.type

object DecodingError extends ErrorEffect[DecodingException, DecodingException]:
  def badRequest: Handler[Const[Response], Const[Response], DecodingError, Any] =
    handlers.first
    .project[Response]
    .mapK([_] => (ee: Either[DecodingException, Response]) => ee match
      case Right(r) => r
      case Left(e) => Response(Status.BadRequest).withText(e.getMessage)
    )
