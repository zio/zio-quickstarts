package dev.zio.quickstart

import dev.zio.quickstart.counter.CounterApp
import dev.zio.quickstart.download.DownloadApp
import dev.zio.quickstart.greet.GreetingApp
import dev.zio.quickstart.users.{InmemoryUserRepo, PersistentUserRepo, UserApp}
import zio._
import zio.http._

object MainApp extends ZIOAppDefault:
  def run: ZIO[Environment with ZIOAppArgs with Scope, Throwable, Any] =
    val httpApps = GreetingApp() ++ DownloadApp() ++ CounterApp() ++ UserApp()
    Server
      .serve(
        httpApps.withDefaultErrorResponse
      )
      .provide(
        Server.defaultWithPort(8080),

        // An layer responsible for storing the state of the `counterApp`
        ZLayer.fromZIO(Ref.make(0)),

        // To use the persistence layer, provide the `PersistentUserRepo.layer` layer instead
        InmemoryUserRepo.layer
      )
