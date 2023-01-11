package dev.zio.quickstart.users

import zio.json._

case class User(id: String, firstName: String)

object User {
  implicit val encoder:  JsonEncoder[User] = DeriveJsonEncoder.gen[User]
  implicit val decoder: JsonDecoder[User] = DeriveJsonDecoder.gen[User]
  //todo def not right
//  implicit val encoderL: JsonEncoder[Iterator[User]] = DeriveJsonEncoder.gen[Iterator[User]]
}
