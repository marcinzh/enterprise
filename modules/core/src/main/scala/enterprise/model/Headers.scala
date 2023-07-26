package enterprise.model


final class Headers(val asSeq: Vector[Header], val asMap: Map[HeaderKey, List[Header]]):
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

  def get[H <: Header](key: HeaderKey.Apply[H]): Option[H] = asMap.get(key).flatMap(_.headOption).map(_.downcast[H])
  
  def apply[H <: Header](key: HeaderKey.Apply[H]): H = get(key).get

  override def toString = asSeq.iterator.map(_.render).mkString("{", ", ", "}")


object Headers:
  def apply(headers: Header*): Headers = headers.foldLeft(empty)(_ add _)
  def empty: Headers = new Headers(Vector(), Map())
