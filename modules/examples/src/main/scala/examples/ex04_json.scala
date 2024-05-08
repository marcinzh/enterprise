//> using scala "3.3.3"
//> using dep "io.github.marcinzh::enterprise-core:0.3.0"
package examples
import turbolift.Extensions._
import enterprise.{Request, Response, Router, Status}
import enterprise.effects.ErrorResponse
import enterprise.server.{Server, Config}
import enterprise.DSL._
import kirk.Exports._
import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._


@main def ex04 =
  case class Person(name: String, age: Int)
  given codec: JsonValueCodec[Person] = JsonCodecMaker.make

  Server:
    Router:
      case GET / "encode" / name / rawAge =>
        for
          age <- ErrorResponse.fromOption(rawAge.toIntOption)(Response(Status.BadRequest))
          person = Person(name, age)
          response = Response.json(person)
        yield response

      case GET / "decode" =>
        for
          person <- Request.asks(_.body.json[Person]).flatten
          response = Response.text(s"$person")
        yield response
  
    .handleWith:
      Router.handler &&&!
      ErrorResponse.handler &&&!
      DecodingError.badRequest
  .handleWith:
    Server.undertow &&&!
    Config.localhost(9000).toHandler
  .unsafeRunST
