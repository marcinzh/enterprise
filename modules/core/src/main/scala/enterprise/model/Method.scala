package enterprise.model


enum Method:
  case GET
  case PUT
  case POST
  case DELETE
  case Unknown(raw: String)

  def render: String =
    this match
      case Unknown(raw) => raw
      case _ => toString


object Method:
  val all = IArray[Method](
    GET,
    PUT,
    POST,
    DELETE,
  )

  private val byUpperCase = all.map(x => x.render -> x).toMap

  def parse(text: String): Method = byUpperCase.get(text.toUpperCase).getOrElse(Unknown(text))
