package enterprise.model
import enterprise.internals.HeaderParser


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
