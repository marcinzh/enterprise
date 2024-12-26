package enterprise.server
import turbolift.{!!, Signature, Effect, Handler}
import turbolift.Extensions._
import turbolift.effects.IO
import turbolift.io.{Warp, Loom}
import enterprise.{Request, Response, Service}


trait ServerSignature extends Signature:
  def serve[U](service: Service[U]): Unit !! (ThisEffect & U)
  def serveLoom[U](service: Service[U]): Loom[Unit, U & IO] !! (ThisEffect & ConfigEffect & Warp & IO)


case object ServerEffect extends Effect[ServerSignature] with ServerSignature:
  final override def serve[U](service: Service[U]): Unit !! (this.type & U) = perform(_.serve(service))
  final override def serveLoom[U](service: Service[U]): Loom[Unit, U & IO] !! (this.type & ConfigEffect & Warp & IO) = perform(_.serveLoom(service))

  final def makeHandler(server: Server): ThisHandler[Identity, Identity, ConfigEffect & IO] =
    new impl.Proxy[ConfigEffect & IO] with ServerSignature:
      override def serve[U](service: Service[U]): Unit !! (ThisEffect & U) = server.run(service)
      override def serveLoom[U](service: Service[U]): Loom[Unit, U & IO] !! (ThisEffect & ConfigEffect & Warp & IO) = server.runLoom(service)
    .toHandler
