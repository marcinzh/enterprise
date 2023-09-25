//> using scala "3.3.1"
//> using dep "io.github.marcinzh::enterprise-core:0.2.0-SNAPSHOT"
package examples
import turbolift.Extensions._
import enterprise.{Response, Router, Status}
import enterprise.server.{Server, Config}
import enterprise.DSL._


@main def ex02 =
  Server.serve:
    Router:
      case GET / ""            => Response.text("root").pure_!!
      case GET / "slash"       => Response.text("no trailing").pure_!!
      case GET / "slash" / ""  => Response.text("trailing").pure_!!
      case GET / "reverse" / x => Response.text(x.reverse).pure_!!
      case DELETE / "admin"    => Response(Status.Forbidden).pure_!!
    .handleWith:
      Router.handler
  .handleWith:
    Server.undertow &&&!
    Config.localhost(9000).toHandler
  .unsafeRunST
