package kirk
import turbolift.{!!, Handler}
import turbolift.Extensions._
import turbolift.effects.ErrorEffect
import enterprise.{Response, Status}


final class JsonDecodingException(message: String) extends Exception(message)


type JsonDecodingError = JsonDecodingError.type

object JsonDecodingError extends ErrorEffect[JsonDecodingException]:
  def badRequest[U]: Handler[Const[Response[U]], Const[Response[U]], JsonDecodingError, Any] =
    handlers.first
    .project[Response[U]]
    .mapK([_] => (ee: Either[JsonDecodingException, Response[U]]) => ee match
      case Right(r) => r
      case Left(e) => Response(Status.BadRequest).withText(e.getMessage)
    )
