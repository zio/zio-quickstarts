package dev.zio.quickstart

import dev.zio.quickstart.users.*
import zio.*
import zio.http.*
import zio.http.netty.NettyConfig
import zio.http.netty.server.NettyDriver
import zio.schema.codec.JsonCodec.schemaBasedBinaryCodec
import zio.test.*

import java.util.UUID

object UserRouteSpec extends ZIOSpecDefault:
  override def spec: Spec[Any, Any] = suite("UserRoutes")(
    test("create and get user by id") {
      for {
        client <- ZIO.service[Client]
        _      <- TestServer.addRoutes(UserRoutes())
        port   <- ZIO.serviceWith[Server](_.port)
        url      = URL.root.port(port)
        testUser = User("Adam", 28)
        createResponse <- client(
          Request.post(url / "users", Body.from[User](testUser))
        )
        userId      <- createResponse.body.asString(Charsets.Utf8)
        getResponse <- client(Request.get(url / "users" / userId))
        result      <- getResponse.body.to[User]
      } yield assertTrue(result == testUser)
    }.provideSome[Client with Driver with UserRepo](
      TestServer.layer,
      Scope.default,
      InmemoryUserRepo.layer
    )
  ).provide(
    ZLayer.succeed(Server.Config.default.onAnyOpenPort),
    Client.default,
    NettyDriver.customized,
    ZLayer.succeed(NettyConfig.defaultWithFastShutdown),
    InmemoryUserRepo.layer
  )

  override def aspects: Chunk[TestAspectPoly] =
    Chunk(TestAspect.timeout(60.seconds), TestAspect.timed)
