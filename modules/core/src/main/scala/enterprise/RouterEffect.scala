package enterprise
import turbolift.{!!, Handler}
import turbolift.Extensions._
import turbolift.effects.ChoiceEffect


type RouterEffect = RouterEffect.type

case object RouterEffect extends ChoiceEffect:
  def handler[U](notFound: Response !! U): Handler[Const[Response], Const[Response], RouterEffect, U] =
    handlers.first
    .project[Response]
    .mapEffK([_] => (o: Option[Response]) => o.fold(notFound)(!!.pure))

  val handler: Handler[Const[Response], Const[Response], RouterEffect, Any] = handler(defaultNotFound)

  def defaultNotFound: Response !! Any = Response(Status.NotFound).pure_!!
