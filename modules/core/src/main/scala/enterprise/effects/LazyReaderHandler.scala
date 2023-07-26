package enterprise.effects
import turbolift.{!!, Signature, Effect, Handler}
import turbolift.effects.{Reader, ReaderSignature, State}
import turbolift.Extensions._


extension [R](fx: Reader[R])
  def lazyReaderHandler[V](acquire: R !! V): fx.ThisHandler.Id[V] =
    case object S extends State[Option[R]]
    type S = S.type

    new fx.Proxy[S & V] with ReaderSignature[R]:
      def getSome: R !! (S & V) =
        S.get.flatMap:
          case Some(r) => r.pure_!!
          case None => acquire.flatTap(putSome)

      def putSome(r: R): Unit !! S = S.put(Some(r))

      override def ask: R !@! ThisEffect = _ => getSome

      override def asks[A](f: R => A): A !@! ThisEffect = k => ask(k).map(f)

      override def localModify[A, U <: ThisEffect](f: R => R)(body: A !! U): A !@! U =
        k =>
          for
            r <- getSome
            _ <- putSome(f(r))
            a <- k.escape(body)
            _ <- putSome(r)
          yield a

      override def localPut[A, U <: ThisEffect](r: R)(body: A !! U): A !@! U =
        k => localModify(_ => r)(body)(k)

    .toHandler
    .partiallyProvideWith(S.handler(None).dropState)
