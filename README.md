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
type Service[U] = Response !! (RequestEffect & U)
````
Which should be read as: "Computation, that **returns** `Response` and **requests effects**: `RequestEffect` and `U`".
Where `U` means user selected set of effects involved in computing the response.

`RequestEffect` is an instance of Turbolift's `Reader` effect, predefined in Enterprise.

Examples of ptional effects, predefined in Enterprise, are:
- `ResponseError` - An instance of Turbolift's `Error` effect. Allows interruption of processing of the request, returning given `Response` value. 
- `RouterEffect` - An instance of Turbolift's `Choice` effect. Allows defining routes by partial functions. Composition can be done with Turbolift's `++!` operator (similar to `<|>` of `Alternative`).

Such effects can be handled at:
- Request scope: before `.serve` call (i.e. once per each served request). 
- Server scope: after `.serve` call (i.e. once per server launch).

Multiple handlers can be chained, using Turbolift's `&&&!` operator.


## Example

Run the server with `scala-cli`:

> [!IMPORTANT]
> Turbolift requires **Java 11** or newer.

```scala
//> using scala "3.3.4"
//> using dep "io.github.marcinzh::enterprise-core:0.6.0"
package examples
import turbolift.!!
import turbolift.Extensions._
import enterprise.{Response, Router}
import enterprise.DSL._
import enterprise.server.Syntax._
import enterprise.server.undertow.UndertowServer

@main def main =
  Router:
    case GET / "sarcastic" / text =>
      val text2 = text.zipWithIndex.map((c, i) => if i % 2 == 0 then c.toLower else c.toUpper).mkString
      Response.text(text2).pure_!!

  .handleWith(Router.handler)
  .serve
  .handleWith(UndertowServer.handler)
  .handleWith(Config.localhost(9000).handler)
  .runIO
```

Query the server with `HTTPie`:

```bash
http GET "http://localhost:9000/sarcastic/Resistance is futile"
```

---

See [examples folder](./modules/examples/src/main/scala/examples/) for more.
