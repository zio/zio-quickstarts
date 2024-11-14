package dev.zio.quickstart

import Pg._
import zio._
import zio.schema.DeriveSchema

import java.time.LocalDate
import java.util.UUID

object DatabaseQueriesActor {

  object Customerr {

    case class Customer(
        id: UUID,
        firstName: String,
        lastName: String,
        verified: Boolean,
        dob: LocalDate
    )

    implicit val customerschema = DeriveSchema.gen[Customer]
    val customers               = defineTableSmart[Customer]

    val (customerid, firstName, lastName, verified, dob) = customers.columns

    val all: List[Any] =
      customerid :: firstName :: lastName :: verified :: dob :: Nil

  }

  object Order {
    case class Order(
        id: UUID,
        customerId: UUID,
        order_date: LocalDate
    )
    implicit val orderDetailsSchema  = DeriveSchema.gen[Order]
    val orderDetails                 = defineTableSmart[Order]
    val (id, customerId, order_date) = orderDetails.columns
  }

  import Customerr._

  val getFirstCustomerIdDbQuery: ZIO[SqlDriver, Exception, UUID] = {
    val query =
      select(customerid, firstName, lastName, verified, dob)
        .from(customers)
        .to { case (id, firstName, lastName, verified, dob) =>
          Customer(id, firstName, lastName, verified, dob)
        }

    execute(query).runHead.map(_.get.id)
  }

  val getAllCustomerIdsDbQuery: ZIO[SqlDriver, Exception, List[UUID]] = {

    val query =
      select(customerid, firstName, lastName, verified, dob)
        .from(customers)
        .to { case (id, firstName, lastName, verified, dob) =>
          Customer(id, firstName, lastName, verified, dob)
        }

    execute(query).runFold(List.empty[UUID]) { case (list, customer) =>
      list :+ customer.id
    }
  }

  val id = UUID.fromString("60b01fc9-c902-4468-8d49-3c0f989def37")

  def getSingleOrderbyCustomerIdDbQuery(
      customerId: UUID
  ): ZIO[SqlDriver, Exception, Order.Order] = {
    val getSingleOrder =
      select(Order.id, Order.customerId, Order.order_date)
        .from(Order.orderDetails)
        .where(Order.customerId === customerId)
        .to { case (id, customerId, order_date) =>
          Order.Order(id, customerId, order_date)
        }
    execute(getSingleOrder).runCollect.map(_.toList.head)
  }

  def getOrdersByCustomerIdDbQuery(customerIds: List[UUID]) = {
    val getallOrders =
      select(Order.id, Order.customerId, Order.order_date)
        .from(Order.orderDetails)
        .where(Order.customerId in customerIds)
        .to { case (id, customerId, order_date) =>
          Order.Order(id, customerId, order_date)
        }
    execute(getallOrders).runCollect.map(_.toList)

  }

}
