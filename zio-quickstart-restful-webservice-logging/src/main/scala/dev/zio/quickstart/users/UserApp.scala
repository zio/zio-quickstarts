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
  import LogAspect._

  def apply(): Http[UserRepo, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      // POST /users -d '{"name": "John", "age": 35}'
      case req@(Method.POST -> !! / "users") => {
        for {
          body <- req.bodyAsString
          _ <- ZIO.logInfo(s"POST /users -d $body")
          u = body.fromJson[User]
          r <- u match {
            case Left(e) =>
              ZIO.logErrorCause(s"Failed to parse the input", Cause.fail(e))
                .as(Response.text(e).setStatus(Status.BadRequest))
            case Right(u) =>
              UserRepo.register(u)
                .foldCauseZIO(
                  failure =>
                    ZIO.logErrorCause(s"Failed to register user", Cause.fail(failure))
                      .as(Response.status(Status.InternalServerError)),
                  success =>
                    ZIO.logInfo(s"User registered: $success")
                      .as(Response.text(success))
                )
          }
        } yield r
      } @@ logSpan("register-user") @@ logAnnotateCorrelationId(req)

      // GET /users/:id
      case req@(Method.GET -> !! / "users" / id) => {
        for {
          _ <- ZIO.logInfo(s"Request: GET /users/$id")
          r <- UserRepo.lookup(id).some.foldZIO({
            case Some(error) =>
              ZIO.logErrorCause(s"Failed to lookup user.", Cause.fail(error))
                .as(Response.status(Status.InternalServerError))
            case None =>
              ZIO.log(s"Requested user with $id not found")
                .as(Response.status(Status.NotFound))
          },
            success =>
              ZIO.log(s"Retrieved the user")
                .as(Response.json(success.toJson))
          )
        } yield r
      } @@ logSpan("get-user") @@ logAnnotateCorrelationId(req)

      // GET /users
      case req@(Method.GET -> !! / "users") => {
        for {
          _ <- ZIO.logInfo(s"Request: GET /users")
          users <- UserRepo.users.foldCauseZIO(
            failure =>
              ZIO.logErrorCause(s"Failed to retrieve users.", failure)
                .as(Response.status(Status.InternalServerError)),
            success =>
              ZIO.log(s"Retrieved users successfully: response length=${success.length}")
                .as(Response.json(success.toJson))
          )
        } yield users
      } @@ logSpan("get-users") @@ logAnnotateCorrelationId(req)
    }

}
