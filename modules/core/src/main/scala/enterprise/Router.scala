package enterprise
import turbolift.!!


object Router:
  export RouterEffect.handler

  def apply[U <: RouterEffect & RequestEffect](f: PartialFunction[DSL, Response !! U]): Response !! U =
    Request.asks(r => f.lift(DSL(r))).flatMap:
      case None => RouterEffect.empty
      case Some(x) => x

  //@#@ experimental
  def lazily[U](f: PartialFunction[DSL, Response !! U]): Option[Response !! U] !! RequestEffect=
    Request.asks(r => f.lift(DSL(r)))
