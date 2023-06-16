package servicereloader

import zio.macros.ServiceReloader
import zio._

object ServiceReloaderParallelWorkflowExample extends ZIOAppDefault {
  def reloadWorkflow =
    ServiceReloader.reload[Counter].delay(5.seconds)

  def app: ZIO[Counter with ServiceReloader, ServiceReloader.Error, Unit] =
    for {
      _ <- Counter.increment
      _ <- Counter.increment
      _ <- Counter.increment
      _ <- Counter.get.debug("Counter value")

      _ <- ZIO.sleep(6.seconds)

      _ <- Counter.increment
      _ <- Counter.increment
      _ <- Counter.increment
      _ <- Counter.get.debug("Counter value")
    } yield ()

  def run =
    (app <&> reloadWorkflow).provide(Counter.reloadable, ServiceReloader.live)
}
