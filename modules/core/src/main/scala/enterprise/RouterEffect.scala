package enterprise
import turbolift.{!!, Handler}
import turbolift.Extensions._
import turbolift.effects.ChoiceEffect


type RouterEffect = RouterEffect.type

case object RouterEffect extends ChoiceEffect:
  def handler[U](notFound: Response[U] !! U): Handler[Const[Response[U]], Const[Response[U]], RouterEffect, U] =
    handlers.first
    .project[Response[U]]
    .mapEffK([_] => (o: Option[Response[U]]) => o.fold(notFound)(!!.pure))

  def handler: Handler[Const[Response[Any]], Const[Response[Any]], RouterEffect, Any] = handler(defaultNotFound)

  def defaultNotFound: Response[Any] !! Any = Response(Status.NotFound).pure_!!
