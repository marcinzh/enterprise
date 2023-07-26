package enterprise.internals


object HeaderPrettify:
  private val RX = "(?<!^)(?=[A-Z][a-z0-9])".r
  //@#@TODO corrections for: "E-Tag Web-Socket Parent-Span-Id Source-Map Span-Id Trace-Id"
  def apply(name: String): String =
    RX.split(name).mkString("-")
