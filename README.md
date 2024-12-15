[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.marcinzh/enterprise-core_3/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.marcinzh/enterprise-core_3)  [![javadoc](https://javadoc.io/badge2/io.github.marcinzh/enterprise-core_3/javadoc.svg)](https://javadoc.io/doc/io.github.marcinzh/enterprise-core_3)

# Enterprise <image style="max-height:1.2em" src="https://github.githubassets.com/images/icons/emoji/unicode/1f680.png"><image style="max-height:1.2em" src="https://github.githubassets.com/images/icons/emoji/unicode/1fa90.png">

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

Services can also request other effects than these 2. However, they must be handled (eliminated) by the user,
before submitting the service to server. Multiple handlers can be chained, using Turbolift's `&&&!` operator.

Examples of such optional effects, predefined in Enterprise, are:
- `ErrorResponse.Fx`- An instance of Turbolift's `Error` effect. Allows interruption of processing of the request, returning given `Response` value. 
- `Router.Fx` - An instance of Turbolift's `Choice` effect. Allows defining routes by partial functions. Composition can be done with Turbolift's `++!` operator (similar to `<|>` of `Alternative`).

With future version of Turbolift, service will be able to use any number of effects that are handled outside the server scope.


## Example

Run the server with `scala-cli`:

```scala
//> using scala "3.3.4"
//> using dep "io.github.marcinzh::enterprise-core:0.5.0-SNAPSHOT"
import turbolift.!!
import turbolift.Extensions._
import enterprise.{Response, Router}
import enterprise.server.{Server, Config}
import enterprise.DSL._

@main def main =
  Server:
    Router:
      case GET / "sarcastic" / text =>
        val text2 = text.zipWithIndex.map((c, i) => if i % 2 == 0 then c.toLower else c.toUpper).mkString
        Response.text(text2).pure_!!
    .handleWith:
      Router.handler
  .handleWith:
    Server.undertow &&&!
    Config.localhost(9000).toHandler
  .unsafeRunST
```

Query the server with `httpie`:

```bash
http GET http://localhost:9000/sarcastic/assimilate
```

---

See [examples folder](./modules/examples/src/main/scala/examples/) for more.
