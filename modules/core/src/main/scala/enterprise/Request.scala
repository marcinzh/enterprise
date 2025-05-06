package enterprise
import turbolift.effects.IO


type RequestIO = Request[IO]

final case class Request[-U](
  method: Method,
  path: String,
  headers: Headers,
  body: Body[U],
):
  override def toString = s"$method $path"


object Request:
  export RequestEffect.{ask, asks, handler}
