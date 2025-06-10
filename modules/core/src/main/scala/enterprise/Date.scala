package enterprise
import java.time.Instant


opaque type Date = Long

object Date:
  val Epoch: Date = fromEpochSecond(0)

  def fromEpochSecond(n: Long): Date = n
  def fromInstant(instant: Instant): Date = fromEpochSecond(instant.toEpochMilli / 1000)

  extension (thiz: Date)
    def toInstant: Instant = Instant.ofEpochSecond(thiz)
    def toEpochSeconds: Long = thiz
    def render: String = toInstant.toString
