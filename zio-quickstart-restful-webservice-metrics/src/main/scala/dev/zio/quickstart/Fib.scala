package dev.zio.quickstart

import zio._
import zio.metrics.Metric

object Fib extends ZIOAppDefault {
  private val count = Metric.counterInt("fib_call_total").fromConst(1)

  def fib(n: Int): ZIO[Any, Nothing, Int] =
    if (n <= 1) ZIO.succeed(1)
    else
      for {
        a <- fib(n - 1) @@ count
        b <- fib(n - 2) @@ count
      } yield a + b

  def run =
    for {
      i <- Console
        .readLine("Please enter a number to calculate fibonacci: ")
        .mapAttempt(_.toInt)
      n <- fib(i) @@ count
      _ <- Console.printLine(s"fib($i) = $n")
      c <- count.value
      _ <- ZIO.debug(s"number of fib calls to calculate fib($i): ${c.count}")
    } yield ()
}
