
package dev.zio.quickstart

import dev.zio.quickstart.prometheus.PrometheusPublisherApp
import dev.zio.quickstart.users._
import zio._
import zio.http._
import zio.metrics.connectors.{MetricsConfig, prometheus}

object MainApp extends ZIOAppDefault {
  private val metricsConfig = ZLayer.succeed(MetricsConfig(5.seconds))

  def run = {
    Server
      .serve(UserRoutes() ++ PrometheusPublisherApp())
      .provide(
        Server.defaultWithPort(8080),

        // An layer responsible for storing the state of the `counterApp`
        ZLayer.fromZIO(Ref.make(0)),

        // To use the persistence layer, provide the `PersistentUserRepo.layer` layer instead
        InmemoryUserRepo.layer,

        // general config for all metric backend
        metricsConfig,

        // The prometheus reporting layer
        prometheus.publisherLayer,
        prometheus.prometheusLayer
      )
  }
}
