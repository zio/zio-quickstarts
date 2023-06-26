package dev.zio.quickstart.prometheus

import zio._
import zio.http._
import zio.metrics.connectors.prometheus.PrometheusPublisher

object PrometheusPublisherApp {
  def apply(): Http[PrometheusPublisher, Nothing, Request, Response] = {
    Http.collectZIO[Request] { case Method.GET -> Root / "metrics" =>
      ZIO.serviceWithZIO[PrometheusPublisher](_.get.map(Response.text))
    }
  }
}
