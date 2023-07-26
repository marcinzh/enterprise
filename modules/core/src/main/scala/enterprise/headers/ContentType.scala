package enterprise.headers
import enterprise.model.HeaderKey.Companion
import enterprise.model.Header.Constructor
import enterprise.model.{MediaType, Charset}


final case class ContentType(mediaType: MediaType, charset: Option[Charset] = None) extends Constructor[ContentType](ContentType):
  override def renderValue: String =
    charset match
      case None => mediaType.render
      case Some(ch) => s"${mediaType.render}; charset=${ch.toString}"


case object ContentType extends Companion[ContentType]:
  override def parseValue(text: String) = ContentType(MediaType.parse(text))
