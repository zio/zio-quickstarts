package dev.zio.quickstart

import com.dimafeng.testcontainers.{
  JdbcDatabaseContainer,
  PostgreSQLContainer,
  SingleContainer
}
import org.testcontainers.utility.DockerImageName
import zio.sql.{ConnectionPool, ConnectionPoolConfig}
import zio.{Scope, ZIO, ZLayer}
import zio.sql.postgresql.PostgresJdbcModule
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault}

import java.util.Properties

trait JdbcRunnableSpec extends ZIOSpecDefault with PostgresJdbcModule {

  type JdbcEnvironment = TestEnvironment with SqlDriver

  protected def specLayered: Spec[JdbcEnvironment, Object]

  def spec: Spec[TestEnvironment, Any] =
    specLayered.provideCustomShared(jdbcLayer)

  def getContainer: SingleContainer[_] with JdbcDatabaseContainer =
    new PostgreSQLContainer(
      dockerImageNameOverride = Option("postgres").map(DockerImageName.parse)
    ).configure { a =>
      a.withInitScript("init.sql")
      ()
    }

  protected val autoCommit = false

  private[this] def connProperties(
      user: String,
      password: String
  ): Properties = {
    val props = new Properties
    props.setProperty("user", user)
    props.setProperty("password", password)
    props
  }

  private[this] val poolConfigLayer =
    ZLayer.scoped {
      testContainer
        .map(a =>
          ConnectionPoolConfig(
            url = a.jdbcUrl,
            properties = connProperties(a.username, a.password)
          )
        )
    }

  private[this] final lazy val jdbcLayer: ZLayer[Any, Any, SqlDriver] =
    ZLayer.make[SqlDriver](
      connectionPool.orDie,
      SqlDriver.live
    )

  val connectionPool: ZLayer[Any, Throwable, ConnectionPool] =
    poolConfigLayer >>> ConnectionPool.live

  private[this] val testContainer
      : ZIO[Any with Scope, Throwable, SingleContainer[_]
        with JdbcDatabaseContainer] =
    ZIO.acquireRelease {
      ZIO.attemptBlocking {
        val container = getContainer
        container.start()
        container
      }
    } { container =>
      ZIO.attemptBlocking(container.stop()).orDie
    }

}
