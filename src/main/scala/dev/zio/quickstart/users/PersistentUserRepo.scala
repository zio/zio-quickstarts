package dev.zio.quickstart.users

import io.getquill.context.ZioJdbc.DataSourceLayer
import io.getquill.{Escape, H2ZioJdbcContext}
import zio._

import java.util.UUID
import javax.sql.DataSource

case class UserRow(uuid: UUID, name: String, age: Int)

case class PersistentUserRepo(ds: DataSource) extends UserRepo {
  val ctx = new H2ZioJdbcContext(Escape)

  import ctx._

  override def register(user: User): Task[String] = {
    for {
      id <- Random.nextUUID
      _ <- ctx.run {
        quote {
          query[UserRow].insertValue {
            lift(UserRow(id, user.name, user.age))
          }
        }
      }
    } yield id.toString
  }.provideService(ds)

  override def lookup(id: String): Task[Option[User]] =
    ctx.run {
      quote {
        query[UserRow]
          .filter(p => p.uuid == lift(UUID.fromString(id)))
          .map(u => User(u.name, u.age))
      }
    }.provideService(ds).map(_.headOption)

  override def users: Task[List[User]] =
    ctx.run {
      quote {
        query[UserRow].map(u => User(u.name, u.age))
      }
    }.provideService(ds)
}

object PersistentUserRepo {
  def layer: ZLayer[Any, Throwable, PersistentUserRepo] =
    DataSourceLayer.fromPrefix("UserApp") >>>
      ZLayer.fromFunction(PersistentUserRepo(_))
}
