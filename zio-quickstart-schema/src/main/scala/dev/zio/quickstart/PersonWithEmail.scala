package dev.zio.quickstart

import zio._
import zio.schema.Schema.CaseClass1
import zio.schema._
import zio.schema.annotation.validate
import zio.schema.validation._
import zio.stream.ZStream

case class PersonWithEmail(email: String)

object PersonWithEmail {

  val localPart = Regex.letter.atLeast(3)
  val tld = (Regex.literal("org") | Regex.literal("net") | Regex.literal("com")) // Matches top-level domains (2 or more letters)
  val regexValidator = localPart ~ Regex.digit.atLeast(1) ~ Regex.literal("@") ~ Regex.letter.atLeast(3) ~ Regex.literal(".") ~ tld

  implicit val schema: Schema[PersonWithEmail] = CaseClass1(
    id0 = TypeId.fromTypeName("PersonWithEmail"),
    field0 = Schema.Field(
      name0 = "email",
      schema0 = Schema[String],
      validation0 = Validation.regex(regexValidator),
      get0 = (p: PersonWithEmail) => p.email,
      set0 = { (p: PersonWithEmail, s: String) => p.copy(email = s) }
    ),
    defaultConstruct0 = email => PersonWithEmail(email),
  )

}