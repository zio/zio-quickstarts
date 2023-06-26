package servicereloader

import zio._
import zio.macros._

object ServiceReloaderExample extends ZIOAppDefault {

  def app: ZIO[Counter with ServiceReloader, ServiceReloader.Error, Unit] =
    for {
      _ <- Counter.increment
      _ <- Counter.increment
      _ <- Counter.increment
      _ <- Counter.get.debug("Counter value")

      _ <- ServiceReloader.reload[Counter]
      _ <- ZIO.sleep(2.seconds)

      _ <- Counter.increment
      _ <- Counter.increment
      _ <- Counter.increment
      _ <- Counter.get.debug("Counter value")
    } yield ()

  def run = app.provide(Counter.reloadable, ServiceReloader.live)
}
