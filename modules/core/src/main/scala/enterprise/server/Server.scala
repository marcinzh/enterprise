package enterprise.server
import turbolift.!!
import turbolift.Extensions._
import turbolift.effects.IO
import turbolift.io.{Warp, Loom}
import enterprise.{Request, Response, Service}


trait Server:
  def runLoom[U](service: Service[U]): Loom[Unit, U & IO] !! (ConfigEffect & Warp & IO)

  final def apply[U](service: Service[U]): Unit !! (U & ConfigEffect & IO) = run(service)

  final def run[U](service: Service[U]): Unit !! (U & ConfigEffect & IO) = runLoom[U](service).flatMap(_.reduceVoid).warp

  final def handler = ServerEffect.makeHandler(this)
