package enterprise


enum MediaType(val render: String):
  case TextPlain               extends MediaType("text/plain")
  case ApplicationJson         extends MediaType("application/json")
  case ApplicationOctetStream  extends MediaType("application/octet-stream")
  case Unknown(raw: String)    extends MediaType(raw)


object MediaType:
  val all = IArray[MediaType](
    TextPlain,
    ApplicationJson,
    ApplicationOctetStream,
  )

  private val byLowerCase = all.map(x => x.render.toLowerCase -> x).toMap

  def parse(text: String): MediaType = byLowerCase.get(text.toLowerCase).getOrElse(Unknown(text))
