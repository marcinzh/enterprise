package enterprise
import turbolift.{!!, Handler}
import turbolift.Extensions._
import turbolift.effects.Choice


object Router:
  type Fx = Fx.type
  case object Fx extends Choice
  export Fx.empty

  def apply[U <: Fx & Request.Fx](f: PartialFunction[DSL, Response !! U]): Response !! U =
    Request.asks(r => f.lift(DSL(r))).flatMap:
      case None => empty
      case Some(x) => x

  def handler[U](notFound: Response !! U): Handler.FromConst.ToConst[Response, Response, Fx, U] =
    Fx.handlers.first
    .project[Response]
    .mapEffK([_] => (o: Option[Response]) => o.fold(notFound)(!!.pure))

  val handler: Handler.FromConst.ToConst.Free[Response, Response, Fx] = handler(defaultNotFound)

  def defaultNotFound: Response !! Any = Response(Status.NotFound).pure_!!
