package enterprise.effects
import turbolift.{!!, Signature, Effect, Handler}
import turbolift.Extensions._
import turbolift.effects.IO
import enterprise.{Request, Response, Service}
import enterprise.server.{Server, Config}


trait ServerSignature extends Signature:
  def serve[U](service: Service[U]): Unit !! (ThisEffect & U)


case object ServerEffect extends Effect[ServerSignature] with ServerSignature:
  final override def serve[U](service: Service[U]): Unit !! (this.type & U) = perform(_.serve(service))

  final def makeHandler(server: Server): ThisHandler[Identity, Identity, Config.Fx & IO] =
    new impl.Proxy[Config.Fx & IO] with ServerSignature:
      override def serve[U](service: Service[U]): Unit !! (ThisEffect & U) = server.run(service)
    .toHandler
