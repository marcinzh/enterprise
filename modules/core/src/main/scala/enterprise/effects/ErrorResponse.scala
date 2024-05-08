package enterprise.effects
import turbolift.{!!, Handler}
import turbolift.Extensions._
import turbolift.effects.Error
import enterprise.Response


object ErrorResponse:
  case object Fx extends Error[Response]
  type Fx = Fx.type
  export Fx.{raise, catchAll, fromTry, fromOption, fromEither}

  val handler: Handler[Const[Response], Const[Response], Fx, Any] =
    Fx.handlers.first
    .project[Response]
    .mapK([_] => (e: Either[Response, Response]) => e.merge)
