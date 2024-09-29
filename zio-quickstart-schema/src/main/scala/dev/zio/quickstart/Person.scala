package dev.zio.quickstart

import zio.schema._
import zio.schema.annotation.validate
import zio.schema.validation.Validation

// one can choose the detailed way of validation or use annotations

case class Person(
    name: String,
    @validate(Validation.greaterThan(18))
    age: Int
)

object Person {
  implicit val schema: Schema[Person] = DeriveSchema.gen
}
