package enterprise


sealed trait Header:
  def key: HeaderKey
  def renderKey: String
  def renderValue: String
  final def render: String = s"${renderKey}: $renderValue"
  final def downcast[H <: Header] = asInstanceOf[H]


object Header:
  private[enterprise] abstract class Constructor[This <: Header](k: HeaderKey.Apply[This]) extends Header:
    self: This =>
    final override val key: HeaderKey.Apply[This] = k
    final override def renderKey: String = key.render

  final case class Unknown(rawKey: String, rawValue: String) extends Header:
    override def key: HeaderKey.Unknown = HeaderKey.Unknown
    override def renderKey: String = rawKey
    override def renderValue: String = rawValue

  def tryParse(rawKey: String, rawValue: String): Option[Header] =
    for
      key <- HeaderKey.byLowerCase.get(rawKey.toLowerCase)
      value <- key.tryParseValue(rawValue)
    yield value

  def parse(rawKey: String, rawValue: String): Header =
    tryParse(rawKey, rawValue).getOrElse(Unknown(rawKey, rawValue))
