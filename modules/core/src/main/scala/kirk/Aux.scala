package kirk
import scala.reflect.ClassTag
import com.github.plokhotnyuk.jsoniter_scala.core._


object Aux:
  extension [A](self: JsonValueCodec[A])
    def transformOrFail[B](tryFrom: B => Option[A], to: A => B)(using ClassTag[A], ClassTag[B]) : JsonValueCodec[B] =
      transform(b => tryFrom(b).getOrElse(throw new DecodingException(s"Transform from ${summon[ClassTag[B]]} to ${summon[ClassTag[A]]}")), to)

    def transform[B](from: B => A, to: A => B): JsonValueCodec[B] = new:
      def decodeValue(in: JsonReader, default: B): B = to(self.decodeValue(in, from(default)))
      def encodeValue(x: B, out: JsonWriter): Unit = self.encodeValue(from(x), out)
      def nullValue: B = null.asInstanceOf[B]
