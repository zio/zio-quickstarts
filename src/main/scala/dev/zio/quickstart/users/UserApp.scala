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

  def logAnnotateCorrelationId(req: Request): ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] =
    new ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] {
      override def apply[R, E, A](zio: ZIO[R, E, A])(implicit trace: ZTraceElement): ZIO[R, E, A] =
        correlationId(req).flatMap(id => ZIO.logAnnotate("correlation-id", id)(zio))

      def correlationId(req: Request): UIO[String] =
        ZIO.succeed(req.header("X-Correlation-ID").map(_._2.toString))
          .flatMap(x => Random.nextUUID.map(uuid => x.getOrElse(uuid.toString)))
    }

  def apply(): Http[UserRepo, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      // POST /users -d '{"name": "John", "age": 35}'
      case req@(Method.POST -> !! / "users") =>
        (for {
          body <- req.bodyAsString
          _ <- ZIO.logInfo(s"POST /users -d $body")
          u = body.fromJson[User]
          r <- u match {
            case Left(e) =>
              ZIO.logError(s"Failed to parse the input: $e").as(
                Response.text(e).setStatus(Status.BadRequest)
              )
            case Right(u) =>
              UserRepo.register(u)
                .map(id => Response.text(id))
          }
        } yield r) @@ logAnnotateCorrelationId(req)

      // GET /users/:id
      case req@(Method.GET -> !! / "users" / id) =>
        ZIO.logInfo(s"Request: GET /users/$id") *>
          UserRepo.lookup(id)
            .map {
              case Some(user) =>
                Response.json(user.toJson)
              case None =>
                Response.status(Status.NotFound)
            } @@ logAnnotateCorrelationId(req)

      // GET /users
      case req@(Method.GET -> !! / "users") =>
        ZIO.logInfo(s"Request: GET /users") *>
          UserRepo.users.map { response =>
            Response.json(response.toJson)
          } @@ logAnnotateCorrelationId(req)
    }

}
