//> using scala "3.3.4"
//> using dep "io.github.marcinzh::enterprise-core:0.5.0-SNAPSHOT"
package examples
import turbolift.Extensions._
import turbolift.effects.{Random, IO}
import enterprise.{Request, Response, Router, Status}
import enterprise.effects.ErrorResponse
import enterprise.server.{Server, Config}
import enterprise.headers.UserAgent
import enterprise.DSL._


/******************************
http GET http://localhost:9000/whoami
http GET http://localhost:9000/headers
******************************/


@main def ex03_accessingRequest =
  Server:
    Router:
      case GET / "whoami" =>
        for
          request <- Request.ask
          response = request.headers.get(UserAgent) match
            case Some(userAgent) => Response.text(userAgent.value)
            case None => Response(Status.NoContent)
        yield response

      case GET / "headers" =>
        for
          headers <- Request.asks(_.headers.asSeq)
          lines = headers.map(h => s"  ${h.render}")
          text = lines.mkString("Request Headers: {\n", "\n", "\n}")
          response = Response.text(text)
        yield response

    .handleWith:
      Router.handler
  .handleWith:
    Server.undertow &&&!
    Config.localhost(9000).toHandler
  .unsafeRunST

