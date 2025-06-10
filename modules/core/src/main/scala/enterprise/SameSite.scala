package enterprise

enum SameSite(val render: String):
  case Strict extends SameSite("strict")
  case Lax extends SameSite("lax")
  case None extends SameSite("none")
