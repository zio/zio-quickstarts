package dev.zio.quickstart

import dev.zio.quickstart.greet.GreetingApp
import dev.zio.quickstart.users.{User, UserApp, UserRepo, UserService}
import io.github.mbannour.{MongoZioClient, MongoZioCollection}
import zhttp.service.Server
import zio._

object MainApp extends ZIOAppDefault {
  def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] = {

    val userCollection = MongoZioClient
      .autoCloseableClient("mongodb://localhost:27017")
      .map { client =>
        val db = client.getDatabase("ziodb").withCodecRegistry(UserRepo.codecRegistry)
        db.getCollection[User]("test")
      }

    Server.start(
      port = 8080,
      http = UserApp() ++ GreetingApp.authenticate(GreetingApp.apply)
    ).provide(
      UserService.layer,
      UserRepo.layer,
      ZLayer.fromZIO(userCollection),
      Scope.default
    )
  }
}