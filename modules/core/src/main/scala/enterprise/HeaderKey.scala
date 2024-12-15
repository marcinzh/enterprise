package enterprise
import scala.util.Try
import enterprise.headers.All


sealed trait HeaderKey extends Product with Serializable:
  type Value <: Header
  def parseValue(text: String): Value
  def tryParseValue(text: String): Option[Value] = Try(parseValue(text)).toOption
  final val render = HeaderKey.prettify(productPrefix)


object HeaderKey:
  type Apply[H <: Header] = HeaderKey { type Value = H }

  private[enterprise] trait Companion[H <: Header] extends HeaderKey:
    final override type Value = H

  type Unknown = Unknown.type

  case object Unknown extends Companion[Header.Unknown]:
    override def parseValue(text: String): Header.Unknown =
      val rawKey = text.takeWhile(_ != ':')
      val rawValue = text.drop(rawKey.size + 1).trim
      new Header.Unknown(rawKey, rawValue)


  private val RX = "(?<!^)(?=[A-Z][a-z0-9])".r
  private def prettify(name: String): String =
    RX.split(name).mkString("-")

  lazy val byLowerCase: Map[String, HeaderKey] = All.map(hk => hk.render.toLowerCase -> hk).toMap
