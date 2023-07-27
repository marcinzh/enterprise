[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.marcinzh/enterprise-core_3/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.marcinzh/enterprise-core_3)  [![javadoc](https://javadoc.io/badge2/io.github.marcinzh/enterprise-core_3/javadoc.svg)](https://javadoc.io/doc/io.github.marcinzh/enterprise-core_3)
# Enterprise 🚀

Minimalist library for creating HTTP services, using algebraic effects and handlers.


- ⚗️ 🔬 🧪 &nbsp; 𝑷𝑹𝑶𝑻𝑶𝑻𝒀𝑷𝑬 &nbsp;   🚧 WIP 🚧
- Uses Scala 3.
- Uses [Turbolift](https://marcinzh.github.io/turbolift/) as effect system.
- Adapts parts of design of other purely-functional Http libraries (Http4s, ZIO-Http) for the new effect system.


## Design

Services are defined as values of type:
```scala
type MyHttpService = Response !! (Request.Fx & IO)
````
Which should de read as: "Computation, that returns `Response` and requests 2 effects: `Request.Fx` and `IO`"

`Request.Fx`, is an instance of `Reader` effect from Turbolift.

Services can request more effects than those 2, but they must be handled (eliminated) by the user, before submitting the service to server.
Examples of such predefined effects are:
- `Router.Fx` - an instance of `Choice` effect from Turbolift.
- `ErrorResponse.Fx` - an instance of `Error` effect from Turbolift.




## Examples

### 1. Constant response

```scala
//> using scala "3.3.0"
//> using dep "io.github.marcinzh::enterprise-core:0.1.0"
import turbolift.Extensions._
import enterprise.{Response, Server, Config}

@main def main =
  Server.serve:
    Response.text("Live long and prosper").pure_!!
  .handleWith:
    Server.undertow &&&!
    Config.localhost(9000).toHandler
  .unsafeRunST
```

### 2. Add some routing

```scala
//> using scala "3.3.0"
//> using dep "io.github.marcinzh::enterprise-core:0.1.0"
import turbolift.Extensions._
import enterprise.{Response, Server, Config, Router}
import enterprise.DSL._

@main def main =
  Server.serve:
    Router:
      case GET / ""            => Response.text("root").pure_!!
      case GET / "slash"       => Response.text("no trailing").pure_!!
      case GET / "slash" / ""  => Response.text("trailing").pure_!!
      case GET / "reverse" / x => Response.text(x.reverse).pure_!!
    .handleWith:
      Router.handler
  .handleWith:
    Server.undertow &&&!
    Config.localhost(9000).toHandler
  .unsafeRunST
```

### 2. Add more routes and effects
```scala
//> using scala "3.3.0"
//> using dep "io.github.marcinzh::enterprise-core:0.1.0"
import turbolift.Extensions._
import turbolift.effects.{Random, IO}
import enterprise.{Request, Response, ErrorResponse, Router, Server, Config}
import enterprise.model.Status
import enterprise.headers.UserAgent
import enterprise.DSL._


def someRoutes = Router:
  case POST / "random" => Random.nextInt.map(n => Response.text(n.toString))
  case DELETE / "admin" => Response(Status.Forbidden).pure_!!

def moreRoutes = Router:
  case POST / "sleep" / millisRaw =>
    for
      millis <- ErrorResponse.fromOption(millisRaw.toIntOption)(Response(Status.BadRequest))
      _ <- IO(Thread.sleep(millis))
      response = Response()
    yield response

  case GET / "headers" =>
    for
      hs <- Request.asks(_.headers.asSeq)
      lines = hs.map(h => s"  ${h.render}").mkString("Request Headers: {\n", "\n", "\n}")
      response = Response.text(lines)
    yield response


@main def main =
  Server.serve:
    (someRoutes ++! moreRoutes)
    .handleWith:
      Router.handler &&&!
      ErrorResponse.handler &&&!
      Random.handlers.shared
  .handleWith:
    Server.undertow &&&!
    Config.localhost(9000).toHandler
  .unsafeRunST
```
