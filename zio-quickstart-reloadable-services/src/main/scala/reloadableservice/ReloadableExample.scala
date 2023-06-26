package reloadableservice
import zio._

object ReloadableExample extends ZIOAppDefault {
  val app: ZIO[Reloadable[Counter], Any, Unit] =
    for {
      reloadable <- ZIO.service[Reloadable[Counter]]
      counter    <- reloadable.get
      _          <- counter.increment
      _          <- counter.increment
      _          <- counter.increment
      _          <- counter.get.debug("Counter value is")
      _          <- reloadable.reload
      counter    <- reloadable.get
      _          <- counter.increment
      _          <- counter.increment
      _          <- counter.get.debug("Counter value is")
    } yield ()

  def run = app.provide(Counter.reloadable)
}
