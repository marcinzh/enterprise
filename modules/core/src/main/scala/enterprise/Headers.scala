package enterprise
import enterprise.headers.{Cookie, SetCookie}


final class Headers(val asSeq: Vector[Header], val asMap: Map[HeaderKey, List[Header]]):
  lazy val cookies: Map[String, RequestCookie] = get(Cookie).fold(Nil)(_.cookies).iterator.map(c => (c.name, c)).toMap

  def add(header: Header): Headers =
    val seq2 = asSeq :+ header
    val map2 = asMap.updatedWith(header.key)(headersOpt => Some(header :: headersOpt.getOrElse(Nil)))
    new Headers(seq2, map2)

  def put(header: Header): Headers =
    if asMap.contains(header.key) then
      val i = asSeq.indexOf(header)
      val seq2 = asSeq.updated(i, header)
      val map2 = asMap.updated(header.key, header :: Nil)
      new Headers(seq2, map2)
    else
      add(header)

  def apply[H <: Header](key: HeaderKey.Apply[H]): H = get(key).get
  def get[H <: Header](key: HeaderKey.Apply[H]): Option[H] = asMap.get(key).flatMap(_.headOption).map(_.downcast[H])
  def getMany[H <: Header](key: HeaderKey.Apply[H]): List[H] = asMap.get(key).fold(Nil)(_.asInstanceOf[List[H]])

  def getRequestCookie(name: String): Option[RequestCookie] =
    get(Cookie).flatMap(_.cookies.find(_.name == name))

  def getResponseCookie(name: String): Option[ResponseCookie] =
    getMany(SetCookie).map(_.cookie).find(_.name == name)

  def addRequestCookie(cookie: RequestCookie): Headers =
    get(Cookie) match
      case None => add(Cookie(List(cookie)))
      case Some(h) =>
        def loop(xs: List[RequestCookie]): List[RequestCookie] =
          xs match
            case Nil => List(cookie)
            case (cookie2: RequestCookie) :: xs =>
              if cookie2.name == cookie.name then
                cookie :: xs
              else
                cookie2 :: loop(xs)
        put(Cookie(loop(h.cookies)))

  def addResponseCookie(cookie: ResponseCookie): Headers =
    add(SetCookie(cookie))

  override def toString = asSeq.iterator.map(_.render).mkString("{", ", ", "}")


object Headers:
  def apply(headers: Header*): Headers = headers.foldLeft(empty)(_ `add` _)
  def empty: Headers = new Headers(Vector(), Map())
