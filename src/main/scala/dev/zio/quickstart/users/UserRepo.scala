package dev.zio.quickstart.users

import io.github.mbannour.MongoZioCollection
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros
import zio._

case class UserRepo(
    collection: MongoZioCollection[User]
) {

  def getUsers = collection.find.toList

  def putUser(user: User) = collection.insertOne(user).map(_ => user)

}

object UserRepo {
  val layer = ZLayer.fromFunction(UserRepo.apply(_))

  val codecRegistry: CodecRegistry = fromRegistries(
    fromProviders(Macros.createCodecProviderIgnoreNone(classOf[User])),
    DEFAULT_CODEC_REGISTRY
  )
}

//trait UserRepo {
//  def register(user: User): Task[String]
//
//  def lookup(id: String): Task[Option[User]]
//
//  def users: Task[List[User]]
//}
//
//object UserRepo {
//  def register(user: User): ZIO[UserRepo, Throwable, String] =
//    ZIO.serviceWithZIO[UserRepo](_.register(user))
//
//  def lookup(id: String): ZIO[UserRepo, Throwable, Option[User]] =
//    ZIO.serviceWithZIO[UserRepo](_.lookup(id))
//
//  def users: ZIO[UserRepo, Throwable, List[User]] =
//    ZIO.serviceWithZIO[UserRepo](_.users)

