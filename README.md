[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.marcinzh/enterprise-core_3/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.marcinzh/enterprise-core_3)  [![javadoc](https://javadoc.io/badge2/io.github.marcinzh/enterprise-core_3/javadoc.svg)](https://javadoc.io/doc/io.github.marcinzh/enterprise-core_3)
# Enterprise 🚀 🪐

Minimalist library for creating HTTP services, using algebraic effects and handlers.

- ⚗️ 🔬 🧪 &nbsp; 𝑷𝑹𝑶𝑻𝑶𝑻𝒀𝑷𝑬 &nbsp;   🚧 WIP 🚧
- Uses Scala 3.
- Uses [Turbolift](https://marcinzh.github.io/turbolift/) as effect system.
- Adapts parts of design of preexisting purely-functional HTTP libraries ([Http4s](https://github.com/http4s/http4s), [ZIO-Http](https://github.com/zio/zio-http)) for the new effect system.


## Design

Services are defined as values of type:
```scala
type MyHttpService = Response !! (Request.Fx & IO)
````
Which should be read as: "Computation, that **returns** `Response` and **requests 2 effects**: `Request.Fx` and `IO`".

`Request.Fx` is an instance of Turbolift's `Reader` effect, predefined in Enterprise.

Services can also request other effects than these 2. However, they must be handled (eliminated) by the user, before submitting the service to server. Multiple handlers can be chained, using Turbolift's `&&&!` operator.

Examples of such optional effects, predefined in Enterprise, are:
- `ErrorResponse.Fx`- An instance of Turbolift's `Error` effect. Allows interruption of processing of the request, returning given `Response` value. 
- `Router.Fx` - An instance of Turbolift's `Choice` effect. Allows defining routes by partial functions. Composition can be done with Turbolift's `++!` operator (similar to of `<|>` of `Alternative`).


## Examples

Runnable with `scala-cli`.

#### 1. Start with the most basic - constant response:

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

#### 2. Add some routes:

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

#### 3. Add more routes, and use more effects:
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

  case GET / "whoami" =>
    for
      headerOpt <- Request.asks(_.headers.get(UserAgent))
      userAgent <- ErrorResponse.fromOption(headerOpt)(Response(Status.NoContent))
      response = Response.text(userAgent.value)
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
