package dev.zio.quickstart

import dev.zio.quickstart.counter.CounterRoutes
import dev.zio.quickstart.download.DownloadRoutes
import dev.zio.quickstart.greet.GreetingRoutes
import dev.zio.quickstart.users.{
  InmemoryUserRepo,
  PersistentUserRepo,
  UserRoutes
}
import zio._
import zio.http._

object MainApp extends ZIOAppDefault:
  def run =
    for {
      serverFiber <- Server
      .serve(
        GreetingRoutes() ++ DownloadRoutes() ++ CounterRoutes() ++ UserRoutes()
      )
      .provide(
        Server.defaultWithPort(8080),

        // An layer responsible for storing the state of the `counterApp`
        ZLayer.fromZIO(Ref.make(0)),

        // To use the persistence layer, provide the `PersistentUserRepo.layer` layer instead
        InmemoryUserRepo.layer
      ).fork

      // Add a shutdown hook to release the port on exit
      _ <- ZIO.succeed {
        java.lang.Runtime.getRuntime.addShutdownHook(new Thread {
          override def run(): Unit = {
            Unsafe.unsafe { implicit u =>
              Runtime.default.unsafe.run(serverFiber.interrupt)
            }
          }
        })
      }

      // Wait for the server to exit
      _ <- serverFiber.join
    } yield ()
