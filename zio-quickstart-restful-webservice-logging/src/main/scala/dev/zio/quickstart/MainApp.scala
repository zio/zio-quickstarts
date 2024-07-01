package dev.zio.quickstart

import dev.zio.quickstart.counter.CounterApp
import dev.zio.quickstart.download.DownloadApp
import dev.zio.quickstart.greet.GreetingApp
import dev.zio.quickstart.users.{InmemoryUserRepo, PersistentUserRepo, UserApp}
import zio._
import zio.http._
import scala.util.Random
import zio.Console._

object MainApp extends ZIOAppDefault {
  def run = {
    val retries        = 5
    val portRangeStart = 8000
    val portRangeEnd   = 8005

    def allocatePort(retriesLeft: Int): ZIO[Any, Throwable, Unit] =
      if (retriesLeft <= 0) {
        ZIO.fail(
          new RuntimeException(
            "Failed to allocate a port within the retry limit"
          )
        )
      } else {
        val randomPort =
          Random.nextInt(portRangeEnd - portRangeStart + 1) + portRangeStart
        Server
          .serve(
            (GreetingApp() ++ DownloadApp() ++ CounterApp() ++ UserApp()).withDefaultErrorResponse
          )
          .provide(
            Server.defaultWithPort(randomPort),

            // A layer responsible for storing the state of the `counterApp`
            ZLayer.fromZIO(Ref.make(0)),

            // To use the persistence layer, provide the `PersistentUserRepo.layer` layer instead
            InmemoryUserRepo.layer
          ) *> Console
          .printLine("Server started on http://localhost:<port>")
          .catchAll(_ => allocatePort(retriesLeft - 1))
      }

    allocatePort(retries)
      .fold(
        ex => ExitCode.failure,
        _ => ExitCode.success
      )
  }
}
