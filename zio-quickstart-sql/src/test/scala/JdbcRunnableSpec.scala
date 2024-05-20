import com.dimafeng.testcontainers.{JdbcDatabaseContainer, SingleContainer}
import zio.sql.postgresql.PostgresJdbcModule
import zio.sql.{ConnectionPool, ConnectionPoolConfig}
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault}
import zio.{Scope, ZIO, ZLayer}

import java.util.Properties

trait JdbcRunnableSpec extends ZIOSpecDefault with PostgresJdbcModule {

  type JdbcEnvironment = TestEnvironment with SqlDriver

  def specLayered: Spec[JdbcEnvironment, Object]

  protected def getContainer: SingleContainer[_] with JdbcDatabaseContainer

  protected val autoCommit = false

  override def spec: Spec[TestEnvironment, Any] =
    specLayered.provideCustomShared(jdbcLayer)

  private[this] def connProperties(
      user: String,
      password: String
  ): Properties = {
    val props = new Properties
    props.setProperty("user", user)
    props.setProperty("password", password)
    props
  }

  private[this] val poolConfigLayer
      : ZLayer[Any, Throwable, ConnectionPoolConfig] =
    ZLayer.scoped {
      testContainer
        .map(a =>
          ConnectionPoolConfig(
            url = a.jdbcUrl,
            properties = connProperties(a.username, a.password),
            autoCommit = autoCommit
          )
        )
    }

  val connectionPool: ZLayer[Any, Throwable, ConnectionPool] =
    poolConfigLayer >>> ConnectionPool.live

  private[this] final lazy val jdbcLayer: ZLayer[Any, Any, SqlDriver] =
    ZLayer.make[SqlDriver](
      connectionPool.orDie,
      SqlDriver.live
    )

  val testContainer
      : ZIO[Scope, Throwable, SingleContainer[_] with JdbcDatabaseContainer] =
    ZIO.acquireRelease {
      ZIO.attemptBlocking {
        val c = getContainer
        c.start()
        c
      }
    } { container =>
      ZIO.attemptBlocking(container.stop()).orDie
    }
}
