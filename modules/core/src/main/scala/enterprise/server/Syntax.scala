package enterprise.server
import enterprise.Service


object Syntax:
  export enterprise.server.Config

  extension [U](thiz: Service[U])
    def serve = ServerEffect.serve(thiz)
