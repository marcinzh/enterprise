package enterprise
import turbolift.!!


object Router:
  export RouterEffect.handler

  def apply[U <: RouterEffect & RequestEffect, V](f: PartialFunction[DSL, Response[V] !! U]): Response[V] !! U =
    Request.asks(r => f.lift(DSL(r))).flatMap:
      case None => RouterEffect.empty
      case Some(x) => x

  //@#@ experimental
  def lazily[U, V](f: PartialFunction[DSL, Response[V] !! U]): Option[Response[V] !! U] !! RequestEffect =
    Request.asks(r => f.lift(DSL(r)))
