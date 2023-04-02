package dev.zio.quickstart.prometheus

import zhttp.http._
import zio._
import zio.metrics.connectors.prometheus.PrometheusPublisher

object PrometheusPublisherApp {
  def apply(): Http[PrometheusPublisher, Nothing, Request, Response] = {
    Http.collectZIO[Request] { case Method.GET -> !! / "metrics" =>
      ZIO.serviceWithZIO[PrometheusPublisher](_.get.map(Response.text))
    }
  }
}
