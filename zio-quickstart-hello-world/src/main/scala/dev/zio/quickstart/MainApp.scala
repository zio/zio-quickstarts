package dev.zio.quickstart

import java.io.IOException

import zio._

object MainApp extends ZIOAppDefault {
  def run: IO[IOException, Unit] =
    Console.printLine("Hello, World!")
}
