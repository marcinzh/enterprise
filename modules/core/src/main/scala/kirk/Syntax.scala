package kirk
import com.github.plokhotnyuk.jsoniter_scala.core._
import turbolift.!!
import turbolift.Extensions._
import enterprise.{Response, Body}
 

object Syntax:
  extension (thiz: Body)
    def json[T: JsonValueCodec]: T !! DecodingError =
      try
        readFromArray(thiz.bytes).pure_!!
      catch
        case e: JsonReaderException => DecodingError.raise(new DecodingException(e.getMessage))
        case e: DecodingException => DecodingError.raise(e)


  extension (thiz: Body.type)
    def json[T: JsonValueCodec](value: T): Body = Body(writeToArray(value))

  extension (thiz: Response)
    def withJson[T: JsonValueCodec](value: T): Response = thiz.withJsonBody(Body.json(value))

  extension (thiz: Response.type)
    def json[T: JsonValueCodec](value: T): Response = Response.jsonBody(Body.json(value))
