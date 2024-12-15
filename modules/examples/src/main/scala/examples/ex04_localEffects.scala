//> using scala "3.3.4"
//> using dep "io.github.marcinzh::enterprise-core:0.5.0-SNAPSHOT"
package examples
import turbolift.bindless._
import turbolift.effects.{Random, IO}
import enterprise.{Response, Router, Status}
import enterprise.effects.ErrorResponse
import enterprise.DSL._
import enterprise.server.Config
import enterprise.server.Syntax._
import enterprise.server.undertow.UndertowServer


/******************************
http POST http://localhost:9000/random
for i in {1..3}; do http POST http://localhost:9000/sleep/1000 & done; wait
******************************/


@main def ex04_localEffects =
  Router:
    case POST / "random" =>
      `do`:
        val n = Random.nextInt.!
        Response.text(n.toString)

    case POST / "sleep" / millisRaw =>
      `do`:
        val millis = ErrorResponse.fromOption(millisRaw.toIntOption)(Response(Status.BadRequest)).!
        IO.sleep(millis).!
        Response.text(s"Slept ${millis}ms\n")

  .handleWith(Router.handler)
  .handleWith(ErrorResponse.handler)
  .handleWith(Random.handlers.shared)
  .serve
  .handleWith(UndertowServer.toHandler)
  .handleWith(Config.localhost(9000).toHandler)
  .runIO
