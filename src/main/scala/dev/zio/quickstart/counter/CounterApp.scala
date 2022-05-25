package dev.zio.quickstart.counter

import zhttp.http._
import zio.{Ref, ZIO}

// An http app that: 
//  - Requires an environment (`Ref[Int]`)
//  - Doesn't produce errors
//  - Consume a `Request` and produce a `Response`
object CounterApp {
  def apply(): Http[Ref[Int], Nothing, Request, Response] =
    Http.fromZIO(ZIO.service[Ref[Int]]).flatMap { ref =>
      Http.collectZIO[Request] {
        case Method.GET -> !! / "up" =>
          ref.updateAndGet(_ + 1)
            .map(_.toString).map(Response.text)
        case Method.GET -> !! / "down" =>
          ref.updateAndGet(_ - 1)
            .map(_.toString).map(Response.text)
        case Method.GET -> !! / "get" =>
          ref.get.map(_.toString).map(Response.text)
      }
    }
}
