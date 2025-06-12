package enterprise
import turbolift.{!!, Handler}
import turbolift.Extensions._
import turbolift.effects.ChoiceEffect


type RouterEffect = RouterEffect.type

case object RouterEffect extends ChoiceEffect:
  def handlerOpt[U]: Handler[Const[Response[U]], Const[Option[Response[U]]], RouterEffect, Any] =
    handlers.first.project[Response[U]]

  def handler[U](notFound: Response[U] !! U): Handler[Const[Response[U]], Const[Response[U]], RouterEffect, U] =
    handlerOpt.mapEffK([_] => (o: Option[Response[U]]) => o.fold(notFound)(!!.pure))

  def handler: Handler[Const[Response[Any]], Const[Response[Any]], RouterEffect, Any] = handler(defaultNotFound)

  def defaultNotFound: Response[Any] !! Any = Response(Status.NotFound).pure_!!
