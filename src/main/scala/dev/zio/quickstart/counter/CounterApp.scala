package dev.zio.quickstart.counter

import zhttp.http.*
import zio.{Ref, ZIO}

/**
 * An http app that:  
 *  - Accepts `Request` and returns a `Response`
 *  - Does not fail
 *  - Requires the `Ref[Int]` as the environment
 */
object CounterApp:
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
