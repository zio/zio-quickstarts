package dev.zio.quickstart.users

import java.util.UUID
import zio.json.*
import zio.schema._
import zio.schema.DeriveSchema._

case class User(name: String, age: Int)

object User:
  given Schema[User] = DeriveSchema.gen[User]
