package enterprise
import turbolift.{!!, Handler}
import turbolift.Extensions._
import turbolift.effects.Error


object ErrorResponse:
  case object Fx extends Error[Response]
  type Fx = Fx.type
  export Fx.{raise, catchAll, fromTry, fromOption, fromEither}

  val handler: Handler[[_] =>> Response, [_] =>> Response, Fx, Any] =
    Fx.handlers.first
    .project[Response]
    .map([_] => (e: Either[Response, Response]) => e.merge)
