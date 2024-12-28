//> using scala "3.3.4"
//> using dep "io.github.marcinzh::enterprise-core:0.8.0"
//> using dep "io.github.marcinzh::turbolift-bindless:0.112.0"
package examples
import turbolift.bindless._
import turbolift.effects.{Random, IO}
import enterprise.{Response, Router, Status, ResponseError}
import enterprise.DSL._
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
        val millis = ResponseError.fromOption(millisRaw.toIntOption)(Response(Status.BadRequest)).!
        IO.sleep(millis).!
        Response.text(s"Slept ${millis}ms\n")

  .handleWith(Router.handler)
  .handleWith(ResponseError.handler)
  .handleWith(Random.handlers.shared)
  .serve
  .handleWith(UndertowServer.handler)
  .handleWith(Config.localhost(9000).handler)
  .runIO
