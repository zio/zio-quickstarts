package dev.zio.quickstart.users

import java.util.UUID
import zio.json._
import zio.schema.{DeriveSchema, Schema}

case class User(name: String, age: Int)

object User {
  implicit val schema: Schema[User] = 
    DeriveSchema.gen[User]
}
