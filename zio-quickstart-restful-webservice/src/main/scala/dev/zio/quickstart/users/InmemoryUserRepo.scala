package dev.zio.quickstart.users
import zio.*

import scala.collection.mutable

case class InmemoryUserRepo(map: Ref[Map[String, User]]) extends UserRepo:
  def register(user: User): UIO[String] =
    for
      id <- Random.nextUUID.map(_.toString)
      _  <- map.update(_ + (id -> user))
    yield id

  def lookup(id: String): UIO[Option[User]] =
    map.get.map(_.get(id))

  def users: UIO[List[User]] =
    map.get.map(_.values.toList)

object InmemoryUserRepo {
  def layer: ZLayer[Any, Nothing, InmemoryUserRepo] =
    ZLayer.fromZIO(
      Ref.make(Map.empty[String, User]).map(new InmemoryUserRepo(_))
    )
}
