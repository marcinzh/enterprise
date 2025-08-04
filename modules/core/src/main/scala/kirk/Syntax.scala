package kirk
import com.github.plokhotnyuk.jsoniter_scala.core._
import turbolift.!!
import turbolift.effects.IO
import turbolift.Extensions._
import enterprise.{Response, Body}
 

object Syntax:
  extension [U](thiz: Body[U])
    def json[T: JsonValueCodec]: T !! (U & JsonDecodingError & IO) =
      thiz.toArray.flatMap: bytes =>
        IO.catchSomeEff(IO(readFromArray(bytes))):
          case e: JsonReaderException => JsonDecodingError.raise(new JsonDecodingException(e.getMessage))
          case e: JsonDecodingException => JsonDecodingError.raise(e)


  extension (thiz: Body.type)
    def json[T: JsonValueCodec](value: T): Body[Any] = Body.fromBytes(writeToArray(value))

  extension (thiz: Response[?])
    def withJson[T: JsonValueCodec](value: T): Response[Any] = thiz.withJsonBody(Body.json(value))

  extension (thiz: Response.type)
    def json[T: JsonValueCodec](value: T): Response[Any] = Response.jsonBody(Body.json(value))
