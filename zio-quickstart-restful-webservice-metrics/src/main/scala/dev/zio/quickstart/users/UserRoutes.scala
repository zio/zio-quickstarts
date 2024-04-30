package dev.zio.quickstart.users

import zio._
import zio.http._
import zio.schema.codec.JsonCodec.schemaBasedBinaryCodec

/** Collection of routes that:
  *   - Accept a `Request` and returns a `Response`
  *   - May fail with type of `Response`
  *   - Require a `UserRepo` from the environment
  */
object UserRoutes {

  def apply(): Routes[UserRepo, Response] =
    Routes(
      // POST /users -d '{"name": "John", "age": 35}'
      Method.POST / "users" -> handler { (req: Request) =>
        for {
          _ <- ZIO.logInfo(s"The POST /users endpoint called")
          u <- req.body
            .to[User]
            .catchAll(e =>
              ZIO
                .logError(s"Failed to parse the input $e")
                *> ZIO.fail(Response.badRequest("Failed to parse the input!"))
            )
          r <-
            UserRepo
              .register(u)
              .foldZIO(
                e =>
                  ZIO.logError(s"Failed to register user $e") *>
                    ZIO.fail(
                      Response
                        .internalServerError(s"Failed to register the user: $u")
                    ),
                id => ZIO.succeed(Response.text(id))
              )
        } yield r
      },

      // GET /users/:id
      Method.GET / "users" / string("id") -> handler {
        (id: String, _: Request) =>
          ZIO.logInfo(s"The GET /users/$id endpoint called!") *>
            UserRepo
              .lookup(id)
              .foldZIO(
                e =>
                  ZIO
                    .logError(s"Failed to lookup user $e") *>
                    ZIO.fail(
                      Response.internalServerError(s"Cannot retrieve user $id")
                    ),
                {
                  case Some(user) =>
                    ZIO
                      .log(s"Retrieved the user")
                      .as(Response(body = Body.from(user)))
                  case None =>
                    ZIO.log(s"Requested user with $id not found") *>
                      ZIO.fail(Response.notFound(s"User $id not found!"))
                }
              )
      },
      // GET /users
      Method.GET / "users" -> handler {
        UserRepo.users.foldZIO(
          e =>
            ZIO
              .logError(s"Failed to retrieve users. $e") *>
              ZIO.fail(Response.internalServerError("Cannot retrieve users!")),
          users =>
            ZIO
              .log(
                s"Retrieved users successfully: response length=${users.length}"
              )
              .as(Response(body = Body.from(users)))
        )
      }
    ) @@ Middleware.metrics()

}
