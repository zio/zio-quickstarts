package dev.zio.quickstart

import zio._
import zio.stream._

object MainApp extends ZIOAppDefault {
  def run =
    ZStream
      .repeatZIO(Console.readLine("What is your name? "))
      .flatMap {
        case "exit" => ZStream.fail("exit")
        case o      => ZStream.succeed(o)
      }
      .tap(name => Console.printLine(s"Hello $name!"))
      .runDrain
      .catchAll(_ => Console.printLine(s"Exiting..."))

}
