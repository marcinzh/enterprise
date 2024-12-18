//> using scala "3.3.4"
//> using dep "io.github.marcinzh::enterprise-core:0.5.0-SNAPSHOT"
package examples
import turbolift.bindless._
import turbolift.effects.{Random, IO}
import enterprise.{Response, Router, Status}
import enterprise.effects.ErrorResponse
import enterprise.server.{Server, Config}
import enterprise.DSL._


/******************************
http POST http://localhost:9000/random
for i in {1..3}; do http POST http://localhost:9000/sleep/1000 & done; wait
******************************/


@main def ex04_localEffects =
  Server:
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

    .handleWith:
      Router.handler &&&!
      ErrorResponse.handler &&&!
      Random.handlers.shared
  .handleWith:
    Server.undertow &&&!
    Config.localhost(9000).toHandler
  .unsafeRunST


