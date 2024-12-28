//> using scala "3.3.4"
//> using dep "io.github.marcinzh::enterprise-core:0.8.0"
//> using dep "io.github.marcinzh::turbolift-bindless:0.112.0"
package examples
import turbolift.bindless._
import enterprise.{Request, Response, Router, Status, ResponseError}
import enterprise.DSL._
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
        val age = ResponseError.fromOption(rawAge.toIntOption)(invalidAge("not an integer")).!
        if age < 0 then ResponseError.raise(invalidAge("must not be negative")).!
        val person = Person(name, age)
        Response.json(person)

    case GET / "decode" =>
      `do`:
        val body = Request.ask.!.body
        val person = body.json[Person].!
        Response.text(s"$person")

  .handleWith(Router.handler)
  .handleWith(ResponseError.handler)
  .handleWith(DecodingError.badRequest)
  .serve
  .handleWith(UndertowServer.handler)
  .handleWith(Config.localhost(9000).handler)
  .runIO
