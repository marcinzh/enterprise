package enterprise.headers
import enterprise.HeaderKey.Companion
import enterprise.Header.Constructor


final case class ContentLength(length: Long) extends Constructor[ContentLength](ContentLength):
  override def renderValue: String = length.toString


case object ContentLength extends Companion[ContentLength]:
  override def parseValue(text: String) = ContentLength(text.toLong)
