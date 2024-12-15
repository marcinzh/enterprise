//> using scala "3.3.4"
//> using dep "io.github.marcinzh::enterprise-core:0.5.0-SNAPSHOT"
package examples
import turbolift.bindless._
import enterprise.{Request, Response, Router, Status}
import enterprise.effects.ErrorResponse
import enterprise.DSL._
import enterprise.server.Config
import enterprise.server.Syntax._
import enterprise.server.undertow.UndertowServer
import kirk.Exports._
import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._


/******************************
http GET http://localhost:9000/encode/Adam/42
http GET http://localhost:9000/decode name=Adam age:=42
******************************/


@main def ex07_jsonBody =
  case class Person(name: String, age: Int)
  given codec: JsonValueCodec[Person] = JsonCodecMaker.make

  Router:
    case GET / "encode" / name / rawAge =>
      def invalidAge(cause: String) = Response(Status.BadRequest).withText(s"Invalid age: `$rawAge`. Cause: $cause.")
      `do`:
        val age = ErrorResponse.fromOption(rawAge.toIntOption)(invalidAge("not an integer")).!
        if age < 0 then ErrorResponse.raise(invalidAge("must not be negative")).!
        val person = Person(name, age)
        Response.json(person)

    case GET / "decode" =>
      `do`:
        val body = Request.ask.!.body
        val person = body.json[Person].!
        Response.text(s"$person")

  .handleWith(Router.handler)
  .handleWith(ErrorResponse.handler)
  .handleWith(DecodingError.badRequest)
  .serve
  .handleWith(UndertowServer.toHandler)
  .handleWith(Config.localhost(9000).toHandler)
  .runIO
