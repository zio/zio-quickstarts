package dev.zio.quickstart

import zio._
import zio.schema._
import zio.schema.annotation.validate
import zio.schema.validation.Validation
import zio.stream.ZStream

// one can choose the detailed way of validation or use annotations

case class Person(
                  name: String,
                  @validate(Validation.greaterThan(18))
                  age: Int)

object Person {
    implicit val schema : Schema[Person] = DeriveSchema.gen
}  


// one can choose the detailed way of validation or use annotations

//case class Person(name: String, age: Int)

// object Person {
//  implicit val schema: Schema[Person] = CaseClass2(
//    id0 = TypeId.fromTypeName("Person"),
//    field01 = Schema.Field(
//      name0 = "name",
//      schema0 = Schema[String],
//      validation0 = Validation.minLength(15),
//      get0 = (p: Person) => p.name,
//      set0 = { (p: Person, s: String) => p.copy(name = s) }
//    ),
//    field02 = Schema.Field(
//      name0 = "age",
//      schema0 = Schema[Int],
//      validation0 = Validation.greaterThan(18),
//      get0 = (p: Person) => p.age,
//      set0 = { (p: Person, age: Int) => p.copy(age = age) }
//    ),
//    construct0 = (name, age) => Person(name, age),
//  )

// }

