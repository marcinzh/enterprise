//> using scala "3.3.6"
//> using dep "io.github.marcinzh::enterprise-core:0.10.0"
package examples
import turbolift.Extensions._
import enterprise.{Response, Router}
import enterprise.DSL._
import enterprise.server.Syntax._
import enterprise.server.undertow.UndertowServer


/******************************
for i in {1..3}; do http GET http://localhost:9000/$i; done
******************************/


private def myRouter1 = Router:
  case GET / "1" => Response.text("one").pure_!!


private def myRouter2 = Router:
  case GET / "2" => Response.text("two").pure_!!


private def myRouter3 = Router:
  case GET / "3" => Response.text("three").pure_!!


@main def ex02_composingRoutes =
  (myRouter1 ++! myRouter2 ++! myRouter3)
  .handleWith(Router.handler)
  .serve
  .handleWith(UndertowServer.handler)
  .handleWith(Config.localhost(9000).handler)
  .runIO
