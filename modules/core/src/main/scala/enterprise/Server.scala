package enterprise
import turbolift.!!
import turbolift.Extensions._
import turbolift.effects.IO
import enterprise.effects.ServerEffect
import enterprise.server.undertow.UndertowServer


object Server:
  case object Fx extends ServerEffect
  type Fx = Fx.type
  export Fx.serve

  def undertow: Fx.ThisHandler.Id[IO] = Fx.makeHandler(UndertowServer)
