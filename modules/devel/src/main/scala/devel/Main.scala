package devel


object Main:
  def main(args: Array[String]): Unit =
    args.headOption.getOrElse("") match
      case "app0" => App0.run()
      case _ => println("hello world")
