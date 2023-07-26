package enterprise.model


enum Status(val value: Int):
  case Ok                   extends Status(200)
  case Created              extends Status(201)
  case NoContent            extends Status(204)
  case BadRequest           extends Status(400)
  case Unauthorized         extends Status(401)
  case Forbidden            extends Status(403)
  case NotFound             extends Status(404)
  case Gone                 extends Status(410)
  case InternalServerError  extends Status(500)
  case NotImplemented       extends Status(501)
