package enterprise.server
import enterprise.Service
import enterprise.effects.ServerEffect


object Syntax:
  extension [U](thiz: Service[U])
    def serve = ServerEffect.serve(thiz)

    def serveWith(server: Server) =
      server.run(thiz)


