package dev.zio.quickstart

import dev.zio.quickstart.counter.CounterApp
import dev.zio.quickstart.download.DownloadApp
import dev.zio.quickstart.greet.GreetingApp
import dev.zio.quickstart.users.{InmemoryUserRepo, UserApp}
import zhttp.service.Server
import zio._
import zio.logging.LogFormat
import zio.logging.backend.SLF4J

object MainApp extends ZIOAppDefault {
  override val bootstrap =
    SLF4J.slf4j(LogLevel.All, LogFormat.colored)

  def run =
    Server
      .start(
        port = 8081,
        http = GreetingApp() ++ DownloadApp() ++ CounterApp() ++ UserApp()
      )
      .provide(
        // An layer responsible for storing the state of the `counterApp`
        ZLayer.fromZIO(Ref.make(0)),

        // To use the persistence layer, provide the `PersistentUserRepo.layer` layer instead
        InmemoryUserRepo.layer
      )
}
