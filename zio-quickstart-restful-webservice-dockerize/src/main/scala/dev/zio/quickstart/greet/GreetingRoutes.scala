package dev.zio.quickstart.greet
import zio.http._

object GreetingRoutes {
  def apply(): Routes[Any, Nothing] =
    Routes(
      // GET /greet?name=:name
      Method.GET / "greet" -> handler { (req: Request) =>
        if (req.url.queryParams.nonEmpty)
          Response.text(
            s"Hello ${req.url.queryParams("name").map(_.mkString(" and "))}!"
          )
        else Response.badRequest("The name query parameter is missing!")
      },

      // GET /greet
      Method.GET / "greet" -> handler(Response.text(s"Hello World!")),

      // GET /greet/:name
      Method.GET / "greet" / string("name") -> handler {
        (name: String, _: Request) =>
          Response.text(s"Hello $name!")
      }
    )
}
