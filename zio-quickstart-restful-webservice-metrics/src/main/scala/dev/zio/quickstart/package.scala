package dev.zio

import zio.metrics.{Metric, MetricLabel}

package object quickstart {

  def countAllRequests(method: String, handler: String) =
    Metric
      .counterInt("count_all_requests")
      .fromConst(1)
      .tagged(
        MetricLabel("method", method),
        MetricLabel("handler", handler)
      )

}
