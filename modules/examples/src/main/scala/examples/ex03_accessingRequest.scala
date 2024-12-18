//> using scala "3.3.4"
//> using dep "io.github.marcinzh::enterprise-core:0.5.0-SNAPSHOT"
package examples
import turbolift.bindless._
import enterprise.{Request, Response, Router, Status}
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
        `do`:
          val request = Request.ask.!
          request.headers.get(UserAgent) match
            case Some(userAgent) => Response.text(userAgent.value)
            case None => Response(Status.NoContent)

      case GET / "headers" =>
        `do`:
          val headers = Request.ask.!.headers
          val lines = headers.asSeq.map(h => s"  ${h.render}")
          val text = lines.mkString("Request Headers: {\n", "\n", "\n}")
          Response.text(text)

    .handleWith:
      Router.handler
  .handleWith:
    Server.undertow &&&!
    Config.localhost(9000).toHandler
  .unsafeRunST

