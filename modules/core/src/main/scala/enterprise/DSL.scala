package enterprise


opaque type DSL = DSL./

object DSL:
  final case class /(lhs: Method | /, rhs: String)

  export Method._

  def apply(request: Request[?]): DSL = apply(request.method, request.path)

  def apply(method: Method, path: String): DSL =
    assert(path.startsWith("/"))
    if path == "/" then
      /(method, "")
    else
      val it = path.split('/').iterator
      val _ = it.next()
      val dsl = it.foldLeft(method: Method | /)(/(_, _))
      if path.last == '/' then
        /(dsl, "")
      else
        dsl.asInstanceOf[/]
