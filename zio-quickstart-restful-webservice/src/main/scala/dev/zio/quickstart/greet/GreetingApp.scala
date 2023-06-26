package dev.zio.quickstart.greet

import zio.http._

/** An http app that:
  *   - Accepts a `Request` and returns a `Response`
  *   - Does not fail
  *   - Does not use the environment
  */
object GreetingApp:
  def apply(): Http[Any, Nothing, Request, Response] =
    Http.collect[Request] {
      // GET /greet?name=:name
      case req @ (Method.GET -> Root / "greet")
          if (req.url.queryParams.nonEmpty) =>
        Response.text(
          s"Hello ${req.url.queryParams.get("name").map(_.mkString(" and "))}!"
        )

      // GET /greet
      case Method.GET -> Root / "greet" =>
        Response.text(s"Hello World!")

      // GET /greet/:name
      case Method.GET -> Root / "greet" / name =>
        Response.text(s"Hello $name!")
    }
