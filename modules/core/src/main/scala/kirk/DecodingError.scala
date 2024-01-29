package kirk
import turbolift.{!!, Handler}
import turbolift.Extensions._
import turbolift.effects.Error
import enterprise.{Response, Status}


final class DecodingException(message: String) extends Exception(message)


object DecodingError:
  case object Fx extends Error[DecodingException]
  type Fx = Fx.type

  def badRequest: Handler.FromConst.ToConst.Free[Response, Response, Fx] =
    Fx.handlers.first
    .project[Response]
    .mapK([_] => (ee: Either[DecodingException, Response]) => ee match
      case Right(r) => r
      case Left(e) => Response(Status.BadRequest).withText(e.getMessage)
    )
