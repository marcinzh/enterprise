package enterprise.internals
import enterprise.headers.All
import enterprise.model.Header


object HeaderParser:
  private val allKeysByLowerCase = All.map(hk => hk.render.toLowerCase -> hk).toMap

  def tryParse(rawKey: String, rawValue: String): Option[Header] =
    for
      key <- allKeysByLowerCase.get(rawKey.toLowerCase)
      value <- key.tryParseValue(rawValue)
    yield value

  def parse(rawKey: String, rawValue: String): Header =
    tryParse(rawKey, rawValue).getOrElse(Header.Unknown(rawKey, rawValue))

