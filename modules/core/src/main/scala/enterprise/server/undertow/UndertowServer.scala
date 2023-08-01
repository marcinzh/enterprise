package enterprise.server.undertow
import scala.jdk.CollectionConverters._
import scala.util.{Try, Success, Failure}
import io.undertow.Undertow
import io.undertow.server.{HttpHandler, HttpServerExchange}
import io.undertow.util.{HeaderMap, HttpString, SameThreadExecutor}
import turbolift.!!
import turbolift.Extensions._
import turbolift.effects.IO
import enterprise.{Request, Response, Method, Status, Header, Headers, Body}
import enterprise.server.{Server, Config}


object UndertowServer extends Server.Function:
  override def apply(config: Config, app: Response !! (Request.Fx & IO)): Unit !! IO =
    IO:
      Undertow.builder()
      .addHttpListener(config.port, config.host)
      .setHandler(makeHttpHandler(app))
      .build()
      .start()

  private def makeHttpHandler(app: Response !! (Request.Fx & IO)) =
    new HttpHandler:
      override def handleRequest(exchange: HttpServerExchange) =
        exchange.dispatch(SameThreadExecutor.INSTANCE, () => handleRequestAsync(exchange, app))

  private def handleRequestAsync(exchange: HttpServerExchange, app: Response !! (Request.Fx & IO)): Unit =
    exchange.getRequestReceiver.receiveFullBytes: (_, bytes) =>
      Try(readRequest(exchange, bytes)) match
        case Failure(e) => writeBadResponse(exchange, "Failed to inspect request", e)
        case Success(req) =>
          app
          .handleWith(Request.Fx.handler(req))
          .unsafeRunAsync: result =>
            result match
              case Success(rsp) => writeResponse(exchange, rsp)
              case Failure(e) => writeBadResponse(exchange, "Failed to generate response", e)
            exchange.endExchange()

  private def readRequest(exchange: HttpServerExchange, body: Array[Byte]): Request =
    Request(
      method = Method.parse(exchange.getRequestMethod.toString),
      path = exchange.getRequestPath,
      headers = readHeaders(exchange.getRequestHeaders),
      body = Body(body),
    )

  private def writeBadResponse(exchange: HttpServerExchange, message: String, throwable: Throwable): Unit =
    exchange.setStatusCode(Status.InternalServerError.value)
    val stackTrace =
      val sw = new java.io.StringWriter
      val pw = new java.io.PrintWriter(sw)
      throwable.printStackTrace(pw)
      pw.flush()
      sw.toString
    exchange.getResponseSender.send(s"$message.\n\n$stackTrace")

  private def writeResponse(exchange: HttpServerExchange, response: Response): Unit =
    exchange.setStatusCode(response.status.value)
    writeHeaders(exchange.getResponseHeaders, response.headers)
    if response.body.bytes.nonEmpty then
      val bb = java.nio.ByteBuffer.wrap(response.body.bytes)
      exchange.getResponseSender.send(bb)

  private def readHeaders(hmap: HeaderMap): Headers =
    (for
      a <- hmap.getHeaderNames.iterator.asScala
      b <- hmap.get(a).iterator.asScala
      h = Header.parse(a.toString, b)
    yield h)
    .foldLeft(Headers.empty)(_ add _)

  private def writeHeaders(hmap: HeaderMap, headers: Headers): Unit =
    headers.asSeq.foreach: h =>
      hmap.add(new HttpString(h.renderKey), h.renderValue)
