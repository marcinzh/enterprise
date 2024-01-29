package enterprise.server
import turbolift.{!!, Handler}
import turbolift.Extensions._
import turbolift.effects.{Reader, IO}


final case class Config(
  port: Int,
  host: String,
):
  def toHandler = Config.Fx.handler(this)


object Config:
  case object Fx extends Reader[Config]
  type Fx = Fx.type
  export Fx.{ask, asks}

  def localhost(port: Int): Config = Config(port, "localhost")
  def default = localhost(8080)
  def askHandler: Handler.FromId.ToId.Free[Fx] !! Fx = Config.ask.map(Config.Fx.handler(_))
