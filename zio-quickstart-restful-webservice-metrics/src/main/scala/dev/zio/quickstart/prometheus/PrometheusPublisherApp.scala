package dev.zio.quickstart.prometheus

import zio._
import zio.http._
import zio.metrics.connectors.prometheus.PrometheusPublisher

object PrometheusPublisherApp {
  def apply(): Routes[PrometheusPublisher, Nothing] =
    Routes(
      Method.GET / "metrics" -> handler(
        ZIO.serviceWithZIO[PrometheusPublisher](_.get.map(Response.text))
      )
    )
}
