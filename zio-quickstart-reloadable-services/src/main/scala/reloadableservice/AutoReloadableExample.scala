package reloadableservice

import zio._

object AutoReloadableExample extends ZIOAppDefault {
  val app: ZIO[Reloadable[Counter], Any, Unit] =
    for {
      reloadable <- ZIO.service[Reloadable[Counter]]
      counter <- reloadable.get
      _ <- counter.increment
      _ <- counter.increment
      _ <- counter.increment
      _ <- counter.get.debug("Counter value is")
      _ <- ZIO.sleep(6.second)
      counter <- reloadable.get
      _ <- counter.increment
      _ <- counter.increment
      _ <- counter.get.debug("Counter value is")
    } yield ()

  def run = app.provide(Counter.autoReloadable)
}






