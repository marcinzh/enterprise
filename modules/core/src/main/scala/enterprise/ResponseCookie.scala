package enterprise


final case class ResponseCookie(
  name: String,
  content: String,
  expires: Option[Date] = None,
  maxAge: Option[Long] = None,
  domain: Option[String] = None,
  path: Option[String] = None,
  sameSite: Option[SameSite] = None,
  secure: Boolean = false,
  httpOnly: Boolean = false,
):
  def clear: ResponseCookie = copy(content = "", expires = Some(Date.Epoch))

  def render: String =
    List(
      Some(s"$name=$content"),
      renderOpt("Expires", expires.map(_.render)),
      renderOpt("MaxAge", maxAge),
      renderOpt("Domain", domain),
      renderOpt("Path", path),
      renderOpt("SameSite", sameSite),
      renderBool("Secure", secure),
      renderBool("HttpOnly", httpOnly),
    )
    .flatten.mkString("; ")

  private def renderOpt[A](name: String, valueOpt: Option[A]): Option[String] =
    valueOpt.map(a => s"$name=$a")

  private def renderBool(name: String, value: Boolean): Option[String] =
    if value then Some(name) else None


object ResponseCookie:
  def clear(name: String): ResponseCookie =
    ResponseCookie(
      name = name,
      content = "",
      expires = Some(Date.Epoch)
    )
