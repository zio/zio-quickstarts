package dev.zio.quickstart

import DatabaseQueriesActor.Order
import zio.test._

import java.time.LocalDate

object QueryingSpec extends JdbcRunnableSpec {

  import zio._
  import zio.query._

  import java.util.UUID

  case object GetAllUserIds extends Request[Throwable, List[UUID]]

  object CustomerIdDataSource
      extends DataSource.Batched[Any, GetAllUserIds.type] {
    val identifier: String = "UserIdDataSource"

    def run(
        requests: Chunk[GetAllUserIds.type]
    )(implicit trace: Trace): ZIO[Any, Nothing, CompletedRequestMap] =
      (ZIO
        .succeed {
          val result = Queriess.getAllCustomerIds
          result.foldZIO(
            err =>
              ZIO.succeed(
                CompletedRequestMap.empty.insert(GetAllUserIds, Exit.fail(err))
              ),
            ids =>
              ZIO.succeed(
                CompletedRequestMap.empty
                  .insert(GetAllUserIds, Exit.succeed(ids))
              )
          )
        }
        .flatten)
        .provideLayer(jdbcLayer)

    object Queriess {
      def getAllCustomerIds: ZIO[Any, Throwable, List[UUID]] =
        DatabaseQueriesActor.getAllCustomerIdsDbQuery.provideLayer(jdbcLayer)

      def getUserNameById(id: UUID): ZIO[Any, Exception, Order.Order] = {
        DatabaseQueriesActor
          .getSingleOrderbyCustomerIdDbQuery(id)
          .provideLayer(jdbcLayer)
      }
    }

  }

  object UserQueries {
    val getAllUserIds: ZQuery[Any, Throwable, List[UUID]] =
      ZQuery.fromRequest(GetAllUserIds)(CustomerIdDataSource)

    val provisionalResults = List(
      UUID.fromString("60b01fc9-c902-4468-8d49-3c0f989def37"),
      UUID.fromString("f76c9ace-be07-4bf3-bd4c-4a9c62882e64"),
      UUID.fromString("784426a5-b90a-4759-afbb-571b7a0ba35e"),
      UUID.fromString("df8215a2-d5fd-4c6c-9984-801a1b3a2a0b"),
      UUID.fromString("636ae137-5b1a-4c8c-b11f-c47c624d9cdc")
    )
  }

  lazy val jdbcLayer: ZLayer[Any, Nothing, Pg.SqlDriver] =
    ZLayer.make[Pg.SqlDriver](
      connectionPool.orDie,
      Pg.SqlDriver.live
    )

  def specLayered: Spec[QueryingSpec.SqlDriver, Object] = suite("QueryingSpec")(
    test("fetching all user IDs") {
      val allIds = for {
        ids <- UserQueries.getAllUserIds.run

      } yield ids

      for {
        _ <- Console.printLine("Starting something")
        currTime = java.lang.System.nanoTime()
        res <- allIds
        doneTime = java.lang.System.nanoTime() - currTime
        _ <- Console.printLine("with zquery => " + (doneTime / 1000000))
        // _ <- Console.printLine(res)
      } yield assertCompletes && assertTrue(res.size == 5) && assertTrue(
        res == UserQueries.provisionalResults
      )
    }
  )

}
