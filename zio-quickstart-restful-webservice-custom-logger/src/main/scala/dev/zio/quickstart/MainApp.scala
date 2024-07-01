package dev.zio.quickstart

import dev.zio.quickstart.users.{InmemoryUserRepo, UserRoutes}
import zio._
import zio.http._
import zio.logging.LogFormat
import zio.logging.backend.SLF4J
import zio.Console._
import scala.util.Random

object MainApp extends ZIOAppDefault {
  override val bootstrap: ZLayer[Any, Nothing, Unit] =
    SLF4J.slf4j(LogFormat.colored)

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
          .serve(UserRoutes())
          .provide(
            Server.defaultWithPort(randomPort),

            // A layer responsible for storing the state of the `counterApp`
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
  }
}
