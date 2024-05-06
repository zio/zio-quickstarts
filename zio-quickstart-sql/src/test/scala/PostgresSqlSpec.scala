import com.dimafeng.testcontainers.{
  JdbcDatabaseContainer,
  PostgreSQLContainer,
  SingleContainer
}
import org.testcontainers.utility.DockerImageName
import zio.{Cause, Chunk}
import zio.schema.DeriveSchema
import zio.test.Assertion._
import zio.test._

import java.math.BigDecimal
import java.time.{LocalDate, ZonedDateTime}
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

  // define schemes
  object ProductSchema {
    final case class Product(
        id: UUID,
        name: String,
        description: String,
        imageUrl: Option[String] // also work with optional fields
    )
    implicit val productSchema =
      DeriveSchema.gen[Product] // derive schema for model
    val products = defineTableSmart[Product] // derive table instance for model
    val (productId, name, description, imageUrl) =
      products.columns // you can extract all columns from table instance
  }

  object CustomerSchema {
    final case class Customer(
        id: UUID,
        firstName: String,
        lastName: String,
        verified: Boolean,
        dob: LocalDate,
        createdTimestamp: ZonedDateTime = ZonedDateTime.now()
    )
    implicit val customerSchema = DeriveSchema.gen[Customer]
    val customers               = defineTableSmart[Customer]
    val (customerId, firstName, lastName, verified, dob, createdTimestamp) =
      customers.columns

    val ALL =
      customerId ++ firstName ++ lastName ++ verified ++ dob ++ createdTimestamp
  }

  object OrderSchema {
    final case class Order(id: UUID, customerId: UUID, orderDate: LocalDate)
    implicit val orderSchema               = DeriveSchema.gen[Order]
    val orders                             = defineTable[Order]
    val (orderId, fkCustomerId, orderDate) = orders.columns
  }

  object ProductPriceSchema {
    case class ProductPrices(
        productId: UUID,
        effective: LocalDate,
        price: BigDecimal
    )
    implicit val productPricesSchema = DeriveSchema.gen[ProductPrices]
    val productPrices                = defineTableSmart[ProductPrices]
    val (productPricesOrderId, effectiveDate, productPrice) =
      productPrices.columns
  }

  object OrderDetailsSchema {
    case class OrderDetails(
        orderId: UUID,
        productId: UUID,
        quantity: Int,
        unitPrice: BigDecimal
    )
    implicit val orderDetailsSchema = DeriveSchema.gen[OrderDetails]
    val orderDetails                = defineTableSmart[OrderDetails]
    val (orderDetailsOrderId, orderDetailsProductId, quantity, unitPrice) =
      orderDetails.columns
  }

  override def specLayered: Spec[PostgresSqlSpec.JdbcEnvironment, Object] =
    suite("Query")(
      test("run simple select") {
        import ProductSchema._

        val selectQuery = select(name)
          .from(products)
          .where(
            productId === UUID.fromString(
              "4C770002-4C8F-455A-96FF-36A8186D5290"
            )
          ) // type safe building sql query
        val expectedName = "Slippers"

        for {
          result <- execute(
            selectQuery
          ).runHead // return option of one row
        } yield assert(result)(
          isSome(equalTo(expectedName))
        ) // save assert on option
      },
      test("run simple select with mapping into model") {
        import OrderSchema._

        val selectQuery = select(orderId, fkCustomerId, orderDate)
          .from(orders)
          .orderBy(Ordering.Desc(orderDate)) // sorting results by field
          .limit(2)                          // set limit
          .offset(1)                         // set offset

        val expected = Chunk.empty

        for {
          result <- execute(selectQuery)
            .map(Order tupled _)
            .runCollect // return chunk of founded rows
        } yield assert(result)(hasSameElementsDistinct(expected))
      },
      test("run select with join") {
        import ProductSchema._
        import OrderDetailsSchema._

        val orderId = UUID.fromString("D4E77298-D829-4E36-A6A0-902403F4B7D3")
        val joinSelectQuery = select(name)
          .from(
            products
              .join(orderDetails)
              .on(productId === orderDetailsProductId)
          )
          .where(orderDetailsOrderId === orderId)

        val expected = Chunk("Mouse Pad", "Thermometer")

        for {
          result <- execute(joinSelectQuery).runCollect
        } yield assert(result)(hasSameElementsDistinct(expected))
      },
      test("run select with aggregation functions") {
        import AggregationDef._ // to use aggregations
        import OrderDetailsSchema._

        val query = select(
          SumDec(unitPrice) as "totalAmount",
          SumInt(quantity) as "soldQuantity"
        ) // use aliases
          .from(orderDetails)
          .where(
            orderDetailsProductId === UUID.fromString(
              "7368ABF4-AED2-421F-B426-1725DE756895"
            )
          )

        for {
          result <- execute(query).runHead
        } yield assert(result)(isSome(equalTo((new BigDecimal(215.99), 40))))
      },
      test("run custom function") {
        import PostgresFunctionDef._ // to use custom functions

        assertZIO(execute(select(GenRandomUuid)).runHead.some)(!isNull)
      },
      test("run batch insert") {
        import CustomerSchema._

        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val id3 = UUID.randomUUID()
        val id4 = UUID.randomUUID()
        val c1 = Customer(
          id1,
          "fnameCustomer1",
          "lnameCustomer1",
          verified = true,
          LocalDate.now()
        )
        val c2 = Customer(
          id2,
          "fnameCustomer2",
          "lnameCustomer2",
          true,
          LocalDate.now()
        )
        val c3 = Customer(
          id3,
          "fnameCustomer3",
          "lnameCustomer3",
          verified = true,
          LocalDate.now()
        )
        val c4 = Customer(
          id4,
          "fnameCustomer4",
          "lnameCustomer4",
          verified = false,
          LocalDate.now()
        )

        val allCustomer = List(c1, c2, c3, c4)
        val data        = allCustomer.map(Customer.unapply(_).get)
        val query       = insertInto(customers)(ALL).values(data)

        val insertAssertion = for {
          result <- execute(query)
        } yield assert(result)(equalTo(4))
        insertAssertion.mapErrorCause(cause => Cause.stackless(cause.untraced))
      },
      test("render query") {
        import OrderSchema._
        import ProductSchema._

        val selectQuery = select(orderId, name)
          .from(products.join(orders).on(productId === orderId))
          .limit(5)
          .offset(10)
        val selectQueryRender =
          "SELECT \"order\".\"id\", \"products\".\"name\" FROM \"products\" INNER JOIN \"order\" ON \"order\".\"product_id\" = \"products\".\"id\"  LIMIT 5 OFFSET 10"

        val insertQuery =
          insertInto(products)(productId, name, description).values(
            (
              UUID.randomUUID(),
              "Zionomicon",
              "Good book to start your journey in zio"
            )
          )
        val insertQueryRender =
          "INSERT INTO \"products\" (\"id\", \"name\", \"description\") VALUES (?, ?, ?);"

        val uuid = UUID.randomUUID()
        val updateQuery = update(products)
          .set(name, "foo")
          .where(productId === uuid)
        val updateQueryRender =
          s"UPDATE \"products\" SET \"name\" = 'foo', WHERE \"products\".\"id\" = '${uuid.toString}'"

        val deleteQuery = deleteFrom(products).where(productId === uuid)
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
