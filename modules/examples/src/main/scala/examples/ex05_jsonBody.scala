//> using scala "3.3.4"
//> using dep "io.github.marcinzh::enterprise-core:0.5.0-SNAPSHOT"
package examples
import turbolift.!!
import enterprise.{Request, Response, Router, Status}
import enterprise.effects.ErrorResponse
import enterprise.server.{Server, Config}
import enterprise.DSL._
import kirk.Exports._
import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._


/******************************
http GET http://localhost:9000/encode/Adam/42
http GET http://localhost:9000/decode name=Adam age:=42
******************************/


@main def ex05_jsonBody =
  case class Person(name: String, age: Int)
  given codec: JsonValueCodec[Person] = JsonCodecMaker.make

  Server:
    Router:
      case GET / "encode" / name / rawAge =>
        def invalidAge(cause: String) = Response(Status.BadRequest).withText(s"Invalid age: `$rawAge`. Cause: $cause.")
        for
          age <- ErrorResponse.fromOption(rawAge.toIntOption)(invalidAge("not an integer"))
          _ <- !!.when(age < 0)(ErrorResponse.raise(invalidAge("must not be negative")))
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
