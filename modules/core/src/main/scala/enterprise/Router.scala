package enterprise
import turbolift.!!


object Router:
  export RouterEffect.handler

  def apply[U <: RouterEffect & RequestEffect, V](f: PartialFunction[DSL, Response[V] !! U]): Response[V] !! U =
    Request.asks(r => f.lift(DSL(r))).flatMap:
      case None => RouterEffect.empty
      case Some(x) => x


  def apply[U <: RouterEffect & RequestEffect, V](pathPrefix: String)(f: PartialFunction[DSL, Response[V] !! U]): Response[V] !! U =
    withPrefix(pathPrefix)(apply(f))


  /** Alias for [[withPrefix]]. */
  def apply[U <: RouterEffect & RequestEffect, V](pathPrefix: String)(body: Response[V] !! U): Response[V] !! U =
    withPrefix(pathPrefix)(body)


  /** Alias for [[withPrefixes]]. */
  def apply[U <: RouterEffect & RequestEffect, V](pairs: (String, Response[V] !! U)*): Response[V] !! U =
    withPrefixes(pairs*)


  def withPrefix[U <: RouterEffect & RequestEffect, V](pathPrefix: String)(body: Response[V] !! U): Response[V] !! U =
    withPrefixes(pathPrefix -> body)


  def withPrefixes[U <: RouterEffect & RequestEffect, V](pairs: (String, Response[V] !! U)*): Response[V] !! U =
    RequestEffect.asks(_.path).flatMap: path =>
      val found = pairs.collectFirst { case (pathPrefix, body) if path.startsWith(pathPrefix) => (path.drop(pathPrefix.size), body)}
      RouterEffect.fromOption(found)
      .flatMap: (path2, body) =>
        RequestEffect.localModify(_.copy(path = path2))(body)


  //@#@ experimental
  def lazily[U, V](f: PartialFunction[DSL, Response[V] !! U]): Option[Response[V] !! U] !! RequestEffect =
    Request.asks(r => f.lift(DSL(r)))
