package reloadableservice

import zio._

import java.util.UUID

trait Counter {
  def increment: UIO[Unit]

  def get: UIO[Int]
}

object Counter {
  val live: ZLayer[Any, Nothing, Counter] = ZLayer.scoped {
    for {
      id  <- Ref.make(UUID.randomUUID())
      ref <- Ref.make(0)
      service = CounterLive(id, ref)
      _ <- service.acquire
      _ <- ZIO.addFinalizer(service.release)
    } yield service
  }

  Reloadable.auto(live, Schedule.fixed(5.seconds))

  val reloadable: ZLayer[Any, Nothing, Reloadable[Counter]] =
    live.reloadableManual

  val autoReloadable: ZLayer[Any, Nothing, Reloadable[Counter]] =
    live.reloadableAuto(Schedule.fixed(10.seconds))
}

final case class CounterLive(id: Ref[UUID], ref: Ref[Int]) extends Counter {
  def acquire: UIO[Unit] = {
    Random.nextUUID
      .flatMap(n => id.set(n) *> ZIO.debug(s"Acquired counter $n"))
  }

  def increment: UIO[Unit] =
    ref.update(_ + 1)

  def get: UIO[Int] =
    ref.get

  def release: UIO[Unit] =
    id.get.flatMap(id => ZIO.debug(s"Released counter $id"))
}
