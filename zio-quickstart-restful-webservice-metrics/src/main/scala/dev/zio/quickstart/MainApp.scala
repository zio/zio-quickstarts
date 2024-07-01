package dev.zio.quickstart

import dev.zio.quickstart.prometheus.PrometheusPublisherApp
import dev.zio.quickstart.users._
import zio._
import zio.http._
import zio.metrics.connectors.{MetricsConfig, prometheus}
import zio.Console._
import scala.util.Random

object MainApp extends ZIOAppDefault {
  private val metricsConfig = ZLayer.succeed(MetricsConfig(5.seconds))

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
          .serve(UserRoutes() ++ PrometheusPublisherApp())
          .provide(
            Server.defaultWithPort(randomPort),

            // A layer responsible for storing the state of the `counterApp`
            ZLayer.fromZIO(Ref.make(0)),

            // To use the persistence layer, provide the `PersistentUserRepo.layer` layer instead
            InmemoryUserRepo.layer,
      
            // General config for all metric backend
            metricsConfig,
            
            // The Prometheus reporting layer
            prometheus.publisherLayer,
            prometheus.prometheusLayer
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
