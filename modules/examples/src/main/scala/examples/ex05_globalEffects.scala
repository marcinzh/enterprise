//> using scala "3.3.4"
//> using dep "io.github.marcinzh::enterprise-core:0.5.0-SNAPSHOT"
package examples
import turbolift.bindless._
import turbolift.effects.State
import enterprise.Response
import enterprise.effects.ErrorResponse
import enterprise.server.Config
import enterprise.server.Syntax._
import enterprise.server.undertow.UndertowServer


/******************************
for i in {1..5}; do http GET http://localhost:9000; done
******************************/


@main def ex05_globalEffects =
  case object Counter extends State[Int]
  `do`:
    val n = Counter.getModify(_ + 1).!
    Response.text(s"Request #$n")
  .serve
  .handleWith(Counter.handlers.shared(0))
  .handleWith(UndertowServer.toHandler)
  .handleWith(Config.localhost(9000).toHandler)
  .runIO
