package dev.zio.quickstart

import io.netty.handler.codec.http.DefaultHttpHeaders
import zhttp.http._
import zhttp.service.Server
import zio._
import zio.json._
import zio.stream.ZStream

import java.io.File
import scala.collection.mutable

case class User(name: String, age: Int)

object User {
  implicit val encoder: JsonEncoder[User] =
    DeriveJsonEncoder.gen[User]
  implicit val decoder: JsonDecoder[User] =
    DeriveJsonDecoder.gen[User]
}

object MainApp extends ZIOAppDefault {
  // An http app that: 
  //  - Doesn't require any environment
  //  - Doesn't produce any errors
  //  - Consume a `Request` and produce a `Response`
  val greetingApp: Http[Any, Nothing, Request, Response] =
    Http.collect[Request] {

      // GET /greet
      case Method.GET -> !! / "greet" =>
        Response.text(s"Hello World!")

      // GET /greet/:name
      case Method.GET -> !! / "greet" / name =>
        Response.text(s"Hello $name")

      // GET /greet?name=:name
      case req@(Method.GET -> !! / "greet") =>
        Response.text(s"Hello ${req.url.queryParams.get("name")}!")

    }

  // An http app that: 
  //  - Doesn't require any environment
  //  - May produce errors of type `Throwable`
  //  - Consume a `Request` and produce a `Response`
  val downloadApp: Http[Any, Throwable, Request, Response] =
    Http.collectHttp[Request] {
      // GET /download
      case Method.GET -> !! / "download" =>
        for {
          file <- Http.fromZIO(
            ZIO.attemptBlocking(new File("file.txt"))
          )
          r <-
            if (file.exists())
              Http.response(
                Response(
                  Status.Ok,
                  Headers.make(
                    new DefaultHttpHeaders()
                      .add("Content-type", "text/plain")
                  ),
                  HttpData.fromFile(file)
                )
              )
            else Http.response(Response.status(Status.NotFound))
        } yield r


      // Download a large file using streams
      // GET /download/stream
      case Method.GET -> !! / "download" / "stream" =>
        Http.fromStream(ZStream.fromFile(new File("file.txt")))
    }

  // An http app that: 
  //  - Requires an environment (`Ref[Int]`)
  //  - Doesn't produce errors
  //  - Consume a `Request` and produce a `Response`
  val counterApp: Http[Ref[Int], Nothing, Request, Response] =
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

  // An http app that: 
  //  - Requires an environment (`Ref[mutable.Map[String, User]]`)
  //  - May produce errors of type `Throwable`
  //  - Consume a `Request` and produce a `Response`
  val userApp: Http[Ref[mutable.Map[String, User]], Throwable, Request, Response] =
    Http.collectZIO[Request] {
      // POST /register -d '{"name": "John", "age": 35}'
      case req@(Method.POST -> !! / "register") =>
        for {
          u <- req.bodyAsString.map(_.fromJson[User])
          r <- u match {
            case Left(e) =>
              ZIO.debug(s"Failed to parse the input: $e").as(Response.text(e))
            case Right(u) =>
              for {
                id <- Random.nextUUID
                users <- ZIO.service[Ref[mutable.Map[String, User]]]
                _ <- users.update(_ addOne(id.toString, u))
                _ <- users.get.debug(s"Registered user: $u")
              } yield Response.text(s"Registered ${u.name} with id $id")
          }
        } yield r

      // GET /user/:id
      case Method.GET -> !! / "user" / id =>
        for {
          users <- ZIO.service[Ref[mutable.Map[String, User]]]
          _ <- users.get.debug(s"users: $users")
          u <- users.get.map {
            _.get(id) match {
              case Some(value) =>
                Response.json(value.toJson)
              case None =>
                Response.status(Status.NotFound)
            }
          }
        } yield u
    }

  def run =
    Server.start(
      port = 8080,
      http = greetingApp ++ downloadApp ++ counterApp ++ userApp
    ).provide(
      // An layer that contains a `Ref[Int]` for the `counterApp`
      ZLayer.fromZIO(Ref.make(0)),
      // An layer that contains a `Ref[mutable.Map[String, User]]` for the `userApp`
      ZLayer.fromZIO(Ref.make(mutable.Map.empty[String, User]))
    )

}
