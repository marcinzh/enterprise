package runner
import scala.quoted.*


object Macro:
  inline def example(inline f: Unit): (() => Unit, String) =
    ${ exampleImpl('f) }

  private def exampleImpl(f: Expr[Unit])(using Quotes): Expr[(() => Unit, String)] =
    import quotes.reflect.*
    val functionName = f.asTerm match
      case Inlined(_, _, Ident(name)) => name
      case Inlined(_, _, Block(List(Ident(name)), _)) => name
      case _ =>
        report.error("function name expected.")
        throw new Exception("Invalid argument to `example` macro")
    '{ (() => $f, ${Expr(functionName)}) }
