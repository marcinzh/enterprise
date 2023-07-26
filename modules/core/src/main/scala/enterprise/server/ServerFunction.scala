package enterprise.server
import turbolift.!!
import turbolift.effects.IO
import enterprise.{Request, Response, Config}


trait ServerFunction extends Function2[Config, Response !! (Request.Fx & IO), Unit !! IO]
