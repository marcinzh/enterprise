//> using scala "3.3.4"
//> using dep "io.github.marcinzh::enterprise-core:0.8.0"
//> using dep "io.github.marcinzh::turbolift-bindless:0.112.0"
package examples
import turbolift.bindless._
import turbolift.effects.State
import enterprise.{Response, ResponseError}
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
  .handleWith(UndertowServer.handler)
  .handleWith(Config.localhost(9000).handler)
  .runIO
