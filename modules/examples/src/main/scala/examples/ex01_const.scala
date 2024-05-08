//> using scala "3.3.3"
//> using dep "io.github.marcinzh::enterprise-core:0.3.0"
package examples
import turbolift.Extensions._
import enterprise.Response
import enterprise.server.{Server, Config}


@main def ex01 =
  Server:
    Response.text("Live long and prosper").pure_!!
  .handleWith:
    Server.undertow &&&!
    Config.localhost(9000).toHandler
  .unsafeRunST
