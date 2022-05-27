package dev.zio.quickstart

import dev.zio.quickstart.config.HttpServerConfig
import dev.zio.quickstart.counter.CounterApp
import dev.zio.quickstart.download.DownloadApp
import dev.zio.quickstart.greet.GreetingApp
import dev.zio.quickstart.users.{InmemoryUserRepo, UserApp}
import zhttp.service.Server
import zio._

object MainApp extends ZIOAppDefault {
  def run =
    ZIO.service[HttpServerConfig].flatMap { config =>
      Server.start(
        port = config.port,
        http = GreetingApp() ++ DownloadApp() ++ CounterApp() ++ UserApp()
      )
    }.provide(
      // A layer responsible for storing the state of the `counterApp`
      ZLayer.fromZIO(Ref.make(0)),

      // To use the persistence layer, provide the `PersistentUserRepo.layer` layer instead
      InmemoryUserRepo.layer,
     
      // A layer containing the configuration of the http server
      HttpServerConfig.layer
    )
}
