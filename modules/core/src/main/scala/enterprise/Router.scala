package enterprise
import turbolift.{!!, Handler}
import turbolift.Extensions._
import turbolift.effects.ChoiceEffect


object Router:
  type Fx = Fx.type
  case object Fx extends ChoiceEffect
  export Fx.empty

  def apply[U <: Fx & Request.Fx](f: PartialFunction[DSL, Response !! U]): Response !! U =
    Request.asks(r => f.lift(DSL(r))).flatMap:
      case None => empty
      case Some(x) => x

  def handler[U](notFound: Response !! U): Handler[Const[Response], Const[Response], Fx, U] =
    Fx.handlers.first
    .project[Response]
    .mapEffK([_] => (o: Option[Response]) => o.fold(notFound)(!!.pure))

  val handler: Handler[Const[Response], Const[Response], Fx, Any] = handler(defaultNotFound)

  def defaultNotFound: Response !! Any = Response(Status.NotFound).pure_!!
