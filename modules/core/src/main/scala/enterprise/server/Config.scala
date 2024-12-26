package enterprise.server


final case class Config(
  port: Int,
  host: String,
):
  def handler = ConfigEffect.handler(this)


object Config:
  def localhost(port: Int): Config = Config(port, "localhost")
  def default = localhost(8080)
