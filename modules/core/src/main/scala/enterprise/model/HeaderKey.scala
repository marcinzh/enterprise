package enterprise.model
import scala.util.Try
import enterprise.internals.HeaderPrettify
import enterprise.headers.All


sealed trait HeaderKey extends Product with Serializable:
  type Value <: Header
  def parseValue(text: String): Value
  def tryParseValue(text: String): Option[Value] = Try(parseValue(text)).toOption
  final val render = HeaderPrettify(productPrefix)


object HeaderKey:
  type Apply[H <: Header] = HeaderKey { type Value = H }

  def all: IArray[HeaderKey] = All

  private[enterprise] trait Companion[H <: Header] extends HeaderKey:
    final override type Value = H

  type Unknown = Unknown.type

  case object Unknown extends Companion[Header.Unknown]:
    override def parseValue(text: String): Header.Unknown =
      val rawKey = text.takeWhile(_ != ':')
      val rawValue = text.drop(rawKey.size + 1).trim
      new Header.Unknown(rawKey, rawValue)
