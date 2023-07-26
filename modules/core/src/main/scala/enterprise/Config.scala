package enterprise
import turbolift.{!!, Handler}
import turbolift.Extensions._
import turbolift.effects.{Reader, IO}


final case class Config(
  port: Int,
  host: String,
  mode: Mode,
):
  def toHandler = Config.Fx.handler(this)


object Config:
  case object Fx extends Reader[Config]
  type Fx = Fx.type
  export Fx.{ask, asks}

  def localhost(port: Int): Config = Config(port, "localhost", Mode.Development)
  def default = localhost(8080)
  def askHandler: Handler.Free.Id[Fx] !! Fx = Config.ask.map(Config.Fx.handler(_))


enum Mode:
  case Development
  case Production

