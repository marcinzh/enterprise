package enterprise


final case class Body(bytes: Array[Byte]):
  override def toString: String = text
  def text: String = new String(bytes)


object Body:
  val empty: Body = Body(Array.empty[Byte])
  def apply(value: String): Body = Body(value.getBytes)
