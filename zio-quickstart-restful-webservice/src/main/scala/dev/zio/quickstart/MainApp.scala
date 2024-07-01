package dev.zio.quickstart

import dev.zio.quickstart.counter.CounterRoutes
import dev.zio.quickstart.download.DownloadRoutes
import dev.zio.quickstart.greet.GreetingRoutes
import dev.zio.quickstart.users.{
  InmemoryUserRepo,
  PersistentUserRepo,
  UserRoutes
}
import zio._
import zio.Console._
import zio.http._
import scala.util.Random

object MainApp extends ZIOAppDefault:
  def run =
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
            GreetingRoutes() ++ DownloadRoutes() ++ CounterRoutes() ++ UserRoutes()
          )
          .provide(
            Server.defaultWithPort(randomPort),

            // An layer responsible for storing the state of the `counterApp`
            ZLayer.fromZIO(Ref.make(0)),

            // To use the persistence layer, provide the `PersistentUserRepo.layer` layer instead
            InmemoryUserRepo.layer
          )
          .catchAll(_ => allocatePort(retriesLeft - 1))
      }

    allocatePort(retries)
      .fold(
        ex => ExitCode.failure,
        _ => ExitCode.success
      )
