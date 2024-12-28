//> using scala "3.3.4"
//> using dep "io.github.marcinzh::enterprise-core:0.8.0"
package examples
import turbolift.Extensions._
import enterprise.{Response, Router, Status}
import enterprise.DSL._
import enterprise.server.Syntax._
import enterprise.server.undertow.UndertowServer


/******************************
http GET http://localhost:9000/reverse/hello
http DELETE http://localhost:9000/admin
******************************/


@main def ex01_basicRouter =
  Router:
    case GET / ""            => Response.text("root").pure_!!
    case GET / "slash"       => Response.text("no trailing").pure_!!
    case GET / "slash" / ""  => Response.text("trailing").pure_!!
    case GET / "reverse" / x => Response.text(x.reverse).pure_!!
    case DELETE / "admin"    => Response(Status.Forbidden).pure_!!
  .handleWith(Router.handler)
  .serve
  .handleWith(UndertowServer.handler)
  .handleWith(Config.localhost(9000).handler)
  .runIO
