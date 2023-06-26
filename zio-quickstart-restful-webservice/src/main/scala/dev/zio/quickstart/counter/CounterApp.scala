package dev.zio.quickstart.counter

import zio.http._
import zio.{Ref, ZIO, ZLayer}

/** An http app that:
  *   - Accepts `Request` and returns a `Response`
  *   - Does not fail
  *   - Requires the `Ref[Int]` as the environment
  */
object CounterApp:
  def apply(): Http[Ref[Int], Nothing, Request, Response] =
    Http.collectZIO[Request] {
      case Method.GET -> Root / "up" =>
        ZIO.serviceWithZIO[Ref[Int]] { ref =>
          ref
            .updateAndGet(_ + 1)
            .map(_.toString)
            .map(Response.text)
        }
      case Method.GET -> Root / "down" =>
        ZIO.serviceWithZIO[Ref[Int]] { ref =>
          ref
            .updateAndGet(_ - 1)
            .map(_.toString)
            .map(Response.text)
        }
      case Method.GET -> Root / "get" =>
        ZIO.serviceWithZIO[Ref[Int]](ref =>
          ref.get.map(_.toString).map(Response.text)
        )
    }
