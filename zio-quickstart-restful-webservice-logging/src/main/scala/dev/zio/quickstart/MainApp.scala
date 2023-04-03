package dev.zio.quickstart

import dev.zio.quickstart.counter.CounterApp
import dev.zio.quickstart.download.DownloadApp
import dev.zio.quickstart.greet.GreetingApp
import dev.zio.quickstart.users.{InmemoryUserRepo, PersistentUserRepo, UserApp}
import zhttp.service.Server
import zio._

object MainApp extends ZIOAppDefault {
  def run =
    Server
      .start(
        port = 8080,
        http = GreetingApp() ++ DownloadApp() ++ CounterApp() ++ UserApp()
      )
      .provide(
        // An layer responsible for storing the state of the `counterApp`
        ZLayer.fromZIO(Ref.make(0)),

        // To use the persistence layer, provide the `PersistentUserRepo.layer` layer instead
        InmemoryUserRepo.layer
      ) *> Console.printLine("Server started on http://localhost:8080")
}
