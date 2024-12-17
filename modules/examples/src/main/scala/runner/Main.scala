package runner
import Macro.example


object Main:
  def main(args: Array[String]): Unit =
    args.headOption.flatMap(_.toIntOption).flatMap(list.unapply) match
      case Some((f, _)) => f()
      case None =>
        println("Pass an example index as a parameter to run it.")
        for ((f, n), i) <- list.zipWithIndex do
          println(s"  $i: $n")


  private val list =
    import examples._
    List(
      example(ex00_constantResponse),
      example(ex01_basicRouter),
      example(ex02_composingRoutes),
      example(ex03_accessingRequest),
      example(ex04_localEffects),
      example(ex05_jsonBody),
    )
