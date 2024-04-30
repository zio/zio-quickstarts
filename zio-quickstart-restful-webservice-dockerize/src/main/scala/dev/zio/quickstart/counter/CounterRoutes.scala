package dev.zio.quickstart.counter
import zio._
import zio.http._

object CounterRoutes {
  def apply(): Routes[Ref[Int], Nothing] =
    Routes(
      Method.GET / "up" -> handler {
        ZIO.serviceWithZIO[Ref[Int]] { ref =>
          ref
            .updateAndGet(_ + 1)
            .map(_.toString)
            .map(Response.text)
        }
      },
      Method.GET / "down" -> handler {
        ZIO.serviceWithZIO[Ref[Int]] { ref =>
          ref
            .updateAndGet(_ - 1)
            .map(_.toString)
            .map(Response.text)
        }
      },
      Method.GET / "get" -> handler {
        ZIO.serviceWithZIO[Ref[Int]](ref =>
          ref.get.map(_.toString).map(Response.text)
        )
      }
    )
}
