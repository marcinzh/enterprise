package enterprise.server
import turbolift.!!
import turbolift.Extensions._
import turbolift.effects.IO
import enterprise.{Request, Response}
import enterprise.effects.ServerEffect
import enterprise.server.undertow.UndertowServer


object Server:
  case object Fx extends ServerEffect
  type Fx = Fx.type
  export Fx.{serve => apply}

  def undertow: Fx.ThisHandler[Identity, Identity, IO] = Fx.makeHandler(UndertowServer)

  trait Function extends Function2[Config, Response !! (Request.Fx & IO), Unit !! IO]
