//> using scala "3.3.4"
//> using dep "io.github.marcinzh::enterprise-core:0.6.0"
package examples
import turbolift.bindless._
import enterprise.{Request, Response, Router, Status}
import enterprise.DSL._
import enterprise.headers.UserAgent
import enterprise.server.Syntax._
import enterprise.server.undertow.UndertowServer


/******************************
http GET http://localhost:9000/whoami
http GET http://localhost:9000/headers
******************************/


@main def ex03_accessingRequest =
  Router:
    case GET / "whoami" =>
      `do`:
        Request.ask.!.headers.get(UserAgent) match
          case Some(userAgent) => Response.text(userAgent.value)
          case None => Response(Status.NoContent)

    case GET / "headers" =>
      `do`:
        val headers = Request.ask.!.headers
        val lines = headers.asSeq.map(h => s"  ${h.render}")
        val text = lines.mkString("Request Headers: {\n", "\n", "\n}")
        Response.text(text)

  .handleWith(Router.handler)
  .serve
  .handleWith(UndertowServer.handler)
  .handleWith(Config.localhost(9000).handler)
  .runIO
