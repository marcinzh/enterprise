//> using scala "3.3.3"
//> using dep "io.github.marcinzh::enterprise-core:0.3.0-SNAPSHOT"
package examples
import turbolift.Extensions._
import turbolift.effects.{Random, IO}
import enterprise.{Request, Response, Router, Status}
import enterprise.effects.ErrorResponse
import enterprise.server.{Server, Config}
import enterprise.headers.UserAgent
import enterprise.DSL._


def someRoutes = Router:
  case POST / "random" => Random.nextInt.map(n => Response.text(n.toString))
  case POST / "sleep" / millisRaw =>
    for
      millis <- ErrorResponse.fromOption(millisRaw.toIntOption)(Response(Status.BadRequest))
      _ <- IO(Thread.sleep(millis))
      response = Response()
    yield response

def moreRoutes = Router:
  case GET / "whoami" =>
    for
      headerOpt <- Request.asks(_.headers.get(UserAgent))
      userAgent <- ErrorResponse.fromOption(headerOpt)(Response(Status.NoContent))
      response = Response.text(userAgent.value)
    yield response

  case GET / "headers" =>
    for
      hs <- Request.asks(_.headers.asSeq)
      lines = hs.map(h => s"  ${h.render}").mkString("Request Headers: {\n", "\n", "\n}")
      response = Response.text(lines)
    yield response


@main def ex03 =
  Server:
    (someRoutes ++! moreRoutes)
    .handleWith:
      Router.handler &&&!
      ErrorResponse.handler &&&!
      Random.handlers.shared
  .handleWith:
    Server.undertow &&&!
    Config.localhost(9000).toHandler
  .unsafeRunST
