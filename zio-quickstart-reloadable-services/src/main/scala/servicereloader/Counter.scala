package servicereloader

import zio._
import zio.macros._
import java.util.UUID

trait Counter {
  def increment: UIO[Unit]
  def get: UIO[Int]
}

object Counter {
  val increment: ZIO[Counter, Nothing, Unit] =
    ZIO.serviceWithZIO[Counter](_.increment)

  val get: ZIO[Counter, Nothing, RuntimeFlags] =
    ZIO.serviceWithZIO[Counter](_.get)

  val live: ZLayer[Any, Nothing, Counter] = ZLayer.scoped {
    for {
      id  <- Ref.make(UUID.randomUUID())
      ref <- Ref.make(0)
      service = CounterLive(id, ref)
      _ <- service.acquire
      _ <- ZIO.addFinalizer(service.release)
    } yield service
  }

  val reloadable: ZLayer[ServiceReloader, ServiceReloader.Error, Counter] =
    live.reloadable
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
