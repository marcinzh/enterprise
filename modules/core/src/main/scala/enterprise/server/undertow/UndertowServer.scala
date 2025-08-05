package enterprise.server.undertow
import scala.jdk.CollectionConverters._
import scala.util.{Try, Success, Failure}
import io.undertow.Undertow
import io.undertow.server.{HttpHandler, HttpServerExchange}
import io.undertow.util.{HeaderMap, HttpString, SameThreadExecutor}
import turbolift.{!!, Handler}
import turbolift.Extensions._
import turbolift.effects.IO
import turbolift.io.{Warp, Loom}
import turbolift.data.Outcome
import enterprise.{Request, RequestIO, RequestEffect, Response, Service,  Method, Status, Header, Headers, Body}
import enterprise.server.{Server, Config, ConfigEffect}


object UndertowServer extends Server:
  override def runLoom[U](service: Service[U]): Loom[Unit, U & IO] !! (ConfigEffect & IO & Warp) =
    for
      config <- ConfigEffect.ask
      loom <- Loom.create[Unit, U & IO]
      _ <- IO:
        Undertow.builder()
        .addHttpListener(config.port, config.host)
        .setHandler(makeHttpHandler(service, loom))
        .build()
        .start()
    yield loom

  private def makeHttpHandler[U](service: Service[U], loom: Loom[Unit, U & IO]): HttpHandler =
    new HttpHandler:
      override def handleRequest(exchange: HttpServerExchange) =
        exchange.dispatch(SameThreadExecutor.INSTANCE, () => handleRequestAsync(exchange, service, loom))

  private def handleRequestAsync[U](exchange: HttpServerExchange, service: Service[U], loom: Loom[Unit, U & IO]): Unit =
    exchange.getRequestReceiver.receiveFullBytes: (_, bytes) =>
      Try(readRequest(exchange, bytes)) match
        case Failure(e) => writeBadResponse(exchange, "Failed to inspect request", e)
        case Success(req) =>
          loom.unsafeSubmit:
            IO.catchToTry(service.handleWith(RequestEffect.handler(req))).flatMap:
              case Success(rsp) => writeResponse(exchange, rsp)
              case Failure(e) => IO(writeBadResponse(exchange, "Failed to generate response", e))
            .guarantee(IO(exchange.endExchange()))

  private def readRequest(exchange: HttpServerExchange, body: Array[Byte]): RequestIO =
    Request(
      method = Method.parse(exchange.getRequestMethod.toString),
      path = exchange.getRequestPath,
      headers = readHeaders(exchange.getRequestHeaders),
      body = Body.fromBytes(body),
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

  private def writeResponse[U](exchange: HttpServerExchange, response: Response[U]): Unit !! (U & IO) =
    for
      _ <- IO:
        exchange.setStatusCode(response.status.value)
        writeHeaders(exchange.getResponseHeaders, response.headers)
      bytes <- response.body.toArray
      _ <- IO:
        if bytes.nonEmpty then
          val bb = java.nio.ByteBuffer.wrap(bytes)
          exchange.getResponseSender.send(bb)
    yield ()

  private def readHeaders(hmap: HeaderMap): Headers =
    (for
      a <- hmap.getHeaderNames.iterator.asScala
      b <- hmap.get(a).iterator.asScala
      h = Header.parse(a.toString, b)
    yield h)
    .foldLeft(Headers.empty)(_ `add` _)

  private def writeHeaders(hmap: HeaderMap, headers: Headers): Unit =
    headers.asSeq.foreach: h =>
      hmap.add(new HttpString(h.renderKey), h.renderValue)
