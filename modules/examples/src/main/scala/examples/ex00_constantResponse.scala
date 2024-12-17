//> using scala "3.3.4"
//> using dep "io.github.marcinzh::enterprise-core:0.5.0-SNAPSHOT"
package examples
import turbolift.Extensions._
import enterprise.Response
import enterprise.server.{Server, Config}


/******************************
http GET http://localhost:9000
******************************/


@main def ex00_constantResponse =
  Server:
    Response.text("Live long and prosper").pure_!!
  .handleWith:
    Server.undertow &&&!
    Config.localhost(9000).toHandler
  .unsafeRunST
