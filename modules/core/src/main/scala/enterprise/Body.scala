package enterprise
import java.nio.charset.StandardCharsets
import beam.Stream
import turbolift.effects.IO


type Body[U] = Stream[Byte, U]

object Body:
  val empty: Body[Any] = Stream.empty
  def fromText(value: String): Body[Any] = Stream.from(value.getBytes(StandardCharsets.UTF_8))
  def fromBytes(arr: Array[Byte]): Body[Any] = Stream.from(arr)
