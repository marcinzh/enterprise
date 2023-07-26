package enterprise
import turbolift.{!!, Handler}
import turbolift.Extensions._
import turbolift.effects.Choice
import enterprise.model.Status


object Router:
  type Fx = Fx.type
  case object Fx extends Choice
  export Fx.{fail => empty}

  def apply[U <: Fx & Request.Fx](f: PartialFunction[DSL, Response !! U]): Response !! U =
    Request.asks(r => f.lift(DSL(r))).flatMap:
      case None => empty
      case Some(x) => x

  def handler[U](notFound: Response !! U): Handler[[_] =>> Response, [_] =>> Response, Fx, U] =
    Fx.handlers.first
    .project[Response]
    .flatMap([_] => (o: Option[Response]) => o.fold(notFound)(!!.pure))

  val handler: Handler[[_] =>> Response, [_] =>> Response, Fx, Any] = handler(defaultNotFound)

  def defaultNotFound: Response !! Any = Response(Status.NotFound).pure_!!
