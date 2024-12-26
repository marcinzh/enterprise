//> using scala "3.3.4"
//> using dep "io.github.marcinzh::enterprise-core:0.6.0"
package examples
import turbolift.Extensions._
import enterprise.Response
import enterprise.server.Syntax._
import enterprise.server.undertow.UndertowServer


/******************************
http GET http://localhost:9000
******************************/


@main def ex00_constantResponse =
  Response.text("Live long and prosper").pure_!!
  .serve
  .handleWith(UndertowServer.handler)
  .handleWith(Config.localhost(9000).handler)
  .runIO
