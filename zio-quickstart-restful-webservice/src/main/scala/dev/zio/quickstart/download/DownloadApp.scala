package dev.zio.quickstart.download

import zhttp.http.*
import zio.*
import zio.stream.ZStream

/**
 * An http app that: 
 *   - Accepts a `Request` and returns a `Response` 
 *   - May fail with type of `Throwable`
 *   - Does not require any environment
 */
object DownloadApp:
  def apply(): Http[Any, Throwable, Request, Response] =
    Http.collectHttp[Request] {
      // GET /download
      case Method.GET -> !! / "download" =>
        val fileName = "file.txt"
        Http.fromStream(ZStream.fromResource(fileName)).setHeaders(
          Headers(
            ("Content-Type", "application/octet-stream"),
            ("Content-Disposition", s"attachment; filename=${fileName}")
          )
        )

      // Download a large file using streams
      // GET /download/stream
      case Method.GET -> !! / "download" / "stream" =>
        val file = "bigfile.txt"
        Http.fromStream(
          ZStream.fromResource(file)
            .schedule(Schedule.spaced(50.millis))
        ).setHeaders(
          Headers(
            ("Content-Type", "application/octet-stream"),
            ("Content-Disposition", s"attachment; filename=${file}")
          )
        )
    }
