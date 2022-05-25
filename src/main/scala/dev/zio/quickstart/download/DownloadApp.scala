package dev.zio.quickstart.download

import zhttp.http._
import zio.stream.ZStream
import zio.{Schedule, ZIO, durationInt}

import java.io.File

// An http app that: 
//  - Doesn't require any environment
//  - May produce errors of type `Throwable`
//  - Consume a `Request` and produce a `Response`
object DownloadApp {
  def apply(): Http[Any, Throwable, Request, Response] =
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
                  Headers(
                    ("Content-Type", "application/octet-stream"),
                    ("Content-Disposition", s"attachment; filename=${file.getName}")
                  ),
                  HttpData.fromFile(file)
                )
              )
            else Http.response(Response.status(Status.NotFound))
        } yield r

      // Download a large file using streams
      // GET /download/stream
      case Method.GET -> !! / "download" / "stream" =>
        val file = new File("bigfile.txt")
        Http.fromStream(
          ZStream.fromFile(file)
            .schedule(Schedule.spaced(50.millis))
        ).setHeaders(
          Headers(
            ("Content-Type", "application/octet-stream"),
            ("Content-Disposition", s"attachment; filename=${file.getName}")
          )
        )
    }
}
