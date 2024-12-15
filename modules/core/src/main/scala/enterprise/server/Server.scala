package enterprise.server
import turbolift.!!
import turbolift.Extensions._
import turbolift.effects.IO
import turbolift.io.{Warp, Loom}
import enterprise.{Request, Response, Service}
import enterprise.effects.ServerEffect


trait Server:
  def runLoom[U](service: Service[U]): Loom[Unit, U & IO] !! (Config.Fx & Warp & IO)

  final def apply[U](service: Service[U]): Unit !! (U & Config.Fx & IO) = run(service)

  final def run[U](service: Service[U]): Unit !! (U & Config.Fx & IO) = runLoom[U](service).flatMap(_.reduceVoid).warp

  final def toHandler = ServerEffect.makeHandler(this)
