package dev.zio.quickstart.transactionCheck

import zhttp.http.*
import zio.*
import zio.json.*
import zio.stream.ZStream
import zio.stream.ZSink

object TransactionCheckApp {

  def apply(): Http[Any,Throwable, Request, Response] =
    Http.collectZIO[Request] {
      case req@(Method.POST -> !! / "transaction-check") =>
        for
          t <- req.bodyAsString.map(_.fromJson[Transaction])
          r <- t match
            case Left(e) =>
              ZIO.debug(s"Failed to parse request body: $e").as(
                Response.text(e).setStatus(Status.BadRequest)
              )
            case Right(t) =>
              val filename = "blacklist.txt"
              val lines: ZStream[Any, Throwable, String] =
                ZStream.fromIteratorScoped(
                  ZIO.fromAutoCloseable(
                    ZIO.attempt(scala.io.Source.fromResource(filename))
                  ).map(_.getLines())
                )

              val blackListed = lines.find(x => x == t.dst || x == t.src).runCollect.map(_.toList)

              blackListed.foldZIO(
                error => ZIO.debug(s"Fail processing black list: ${error.getMessage}").as(
                  Response.text(error.getMessage).setStatus(Status.BadRequest)
                ),
                success => {
                  if (success.isEmpty)
                    ZIO.succeed("Source and destination of transaction are allowed.")
                      .as(Response.text("Success")
                      .setStatus(Status.Ok))
                  else
                    ZIO.succeed("Source or Destination name of transaction exist in black list")
                      .as(Response.text("Cancel")
                      .setStatus(Status.Ok))
                }
              )

        yield r  
    }

}
