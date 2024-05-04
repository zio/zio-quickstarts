import com.dimafeng.testcontainers.{
  JdbcDatabaseContainer,
  PostgreSQLContainer,
  SingleContainer
}
import org.testcontainers.utility.DockerImageName
import zio.Chunk
import zio.schema.DeriveSchema
import zio.test.Assertion._
import zio.test._

import java.time.LocalDate
import java.util.UUID

object PostgresSqlSpec extends JdbcRunnableSpec {
  override protected def getContainer
      : SingleContainer[_] with JdbcDatabaseContainer =
    new PostgreSQLContainer(
      dockerImageNameOverride =
        Option("postgres:alpine").map(DockerImageName.parse)
    ).configure { a =>
      a.withInitScript("db_schema.sql") // initialize tables with rows
      ()
    }

  final case class Product(
      id: UUID,
      name: String,
      price: Double,
      imageUrl: Option[String] // also work with optional fields
  )

  object ProductSchema {
    implicit val productSchema =
      DeriveSchema.gen[Product] // derive schema for model
    val products = defineTableSmart[Product] // derive table instance for model
    val (id, name, price, imageUrl) =
      products.columns // you can extract all columns from table instance
  }

  final case class Order(
      id: UUID,
      productId: UUID,
      quantity: Int,
      orderDate: LocalDate
  )

  object OrderSchema {
    implicit val orderSchema                 = DeriveSchema.gen[Order]
    val orders                               = defineTable[Order]
    val (orderId, productId, quantity, date) = orders.columns
  }

  override def specLayered: Spec[PostgresSqlSpec.JdbcEnvironment, Object] =
    suite("Query")(
      test("run select") {
        import ProductSchema._

        val selectQuery = select(name)
          .from(products)
          .where(
            id === UUID.fromString("4C770002-4C8F-455A-96FF-36A8186D5290")
          ) // type safe building sql query
        val expectedName = "Slippers"

        for {
          result <- execute(selectQuery).runCollect // return collection
        } yield assert(result)(
          hasSameElementsDistinct(Chunk.single(expectedName))
        )
      },
      test("render query") {
        import OrderSchema._
        import ProductSchema._

        val selectQuery = select(orderId, name)
          .from(products.join(orders).on(productId === id))
          .limit(5)
          .offset(10)
        val selectQueryRender =
          "SELECT \"order\".\"id\", \"products\".\"name\" FROM \"products\" INNER JOIN \"order\" ON \"order\".\"product_id\" = \"products\".\"id\"  LIMIT 5 OFFSET 10"

        val insertQuery = insertInto(products)(id, name, price).values(
          (UUID.randomUUID(), "Zionomicon", 10.5)
        )
        val insertQueryRender =
          "INSERT INTO \"products\" (\"id\", \"name\", \"price\") VALUES (?, ?, ?);"

        val uuid = UUID.randomUUID()
        val updateQuery = update(products)
          .set(name, "foo")
          .set(price, price * 1.1)
          .where(id === uuid)
        val updateQueryRender =
          s"UPDATE \"products\" SET \"name\" = 'foo', \"price\" = \"products\".\"price\" * 1.1 WHERE \"products\".\"id\" = '${uuid.toString}'"

        val deleteQuery = deleteFrom(products).where(id === uuid)
        val deleteQueryRender =
          s"DELETE FROM \"products\" WHERE \"products\".\"id\" = '${uuid.toString}'"

        assertTrue(
          renderRead(selectQuery) == selectQueryRender &&
            renderInsert(insertQuery) == insertQueryRender &&
            renderUpdate(updateQuery) == updateQueryRender &&
            renderDelete(deleteQuery) == deleteQueryRender
        )
      }
    )
}
