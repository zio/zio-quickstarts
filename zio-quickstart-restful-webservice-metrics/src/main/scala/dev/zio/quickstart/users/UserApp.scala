package dev.zio.quickstart.users

import dev.zio.quickstart._
import zhttp.http._
import zio._
import zio.json._

/**
 * An http app that: 
 *   - Accepts a `Request` and returns a `Response`
 *   - May fail with type of `Throwable`
 *   - Uses a `UserRepo` as the environment
 */
object UserApp {
  def apply(): Http[UserRepo, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      // POST /users -d '{"name": "John", "age": 35}'
      case req@(Method.POST -> !! / "users") =>
        (for {
          u <- req.bodyAsString.map(_.fromJson[User])
          r <- u match {
            case Left(e) =>
              ZIO.debug(s"Failed to parse the input: $e").as(
                Response.text(e).setStatus(Status.BadRequest)
              )
            case Right(u) =>
              UserRepo.register(u)
                .map(id => Response.text(id))
          }
        } yield r) @@ countAllRequests("post", "/users")

      // GET /users/:id
      case Method.GET -> !! / "users" / id =>
        UserRepo.lookup(id)
          .map {
            case Some(user) =>
              Response.json(user.toJson)
            case None =>
              Response.status(Status.NotFound)
          } @@ countAllRequests("get", "/users/:id")

      // GET /users
      case Method.GET -> !! / "users" =>
        UserRepo.users
          .map(response => Response.json(response.toJson)) @@
          countAllRequests("get", "/users")
    }

}
