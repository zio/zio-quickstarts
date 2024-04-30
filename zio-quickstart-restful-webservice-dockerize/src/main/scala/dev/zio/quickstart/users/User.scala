package dev.zio.quickstart.users

import zio.schema._

case class User(name: String, age: Int)

object User {
  implicit val schmea: Schema[User] = DeriveSchema.gen[User]
}
