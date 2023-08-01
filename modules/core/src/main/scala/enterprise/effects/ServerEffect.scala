package enterprise.effects
import turbolift.{!!, Signature, Effect, Handler}
import turbolift.Extensions._
import turbolift.effects.IO
import enterprise.{Request, Response}
import enterprise.server.{Server, Config}


trait ServerSignature extends Signature:
  def serve(app: Response !! (Request.Fx & IO)): Unit !@! (ThisEffect & Config.Fx)


trait ServerEffect extends Effect[ServerSignature] with ServerSignature:
  final override def serve(app: Response !! (Request.Fx & IO)): Unit !! (this.type & Config.Fx) = perform(_.serve(app))

  final def makeHandler(f: Server.Function): ThisHandler.Id[IO] =
    new Proxy[IO] with ServerSignature:
      override def serve(app: Response !! (Request.Fx & IO)): Unit !@! (ThisEffect & Config.Fx) =
        k =>
          for
            config <- k.escape(Config.Fx.ask)
            result <- f(config, app)
          yield result
    .toHandler
