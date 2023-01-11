package dev.zio.quickstart.users

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
  def apply(): Http[UserService with Scope, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      case req@(Method.POST -> !! / "users") =>
        for {
          u <- req.bodyAsString.map(_.fromJson[User])
          r <- u match {
            case Left(e) =>
              ZIO.debug(s"Failed to parse the input: $e").as(
                Response.text(e).setStatus(Status.BadRequest)
              )
            case Right(u) =>
              UserService.putUser(u).map(out => Response.text(out.id))
          }
        }
        yield r
//      // GET /users/:id
//      case Method.GET -> !! / "users" / id =>
//        UserRepo.lookup(id)
//          .map {
//            case Some(user) =>
//              Response.json(user.toJson)
//            case None =>
//              Response.status(Status.NotFound)
//          }
      // GET /users
      case Method.GET -> !! / "users" =>
        UserService.getUsers.map(response => Response.json(response.toJson))
    }
}

