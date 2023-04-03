package dev.zio.quickstart

import zio.json._
import zio.test.Assertion._
import zio.test._

object JsonSpec extends ZIOSpecDefault {
  def spec =
    suite("JsonTests")(
      test("decode from string") {
        val json    = "\"John Doe\""
        val decoded = JsonDecoder[String].decodeJson(json)

        assertTrue(decoded == Right("John Doe"))
      },
      test("decode from int") {
        val json    = "123"
        val decoded = JsonDecoder[Int].decodeJson(json)

        assertTrue(decoded == Right(123))
      },
      test("decode from optional value") {
        val json    = "null"
        val decoded = JsonDecoder[Option[Int]].decodeJson(json)
        assertTrue(decoded == Right(None))
      },
      test("decode from array of ints") {
        val json    = "[1, 2, 3]"
        val decoded = json.fromJson[Array[Int]]

        assert(decoded)(isRight(equalTo(Array(1, 2, 3))))
      },
      test("automatic derivation for case classes") {
        case class Person(name: String, age: Int)
        object Person {
          implicit val decoder: JsonDecoder[Person] =
            DeriveJsonDecoder.gen[Person]
          implicit val encoder: JsonEncoder[Person] =
            DeriveJsonEncoder.gen[Person]
        }

        assertTrue(
          (Person("John", 42).toJson == "{\"name\":\"John\",\"age\":42}")
            && ("{\"name\":\"John\",\"age\":42}".fromJson[Person] == Right(
              Person("John", 42)
            ))
        )
      },
      suite("ADT") {
        sealed trait Fruit                   extends Product with Serializable
        case class Banana(curvature: Double) extends Fruit
        case class Apple(poison: Boolean)    extends Fruit

        object Fruit {
          implicit val decoder: JsonDecoder[Fruit] =
            DeriveJsonDecoder.gen[Fruit]

          implicit val encoder: JsonEncoder[Fruit] =
            DeriveJsonEncoder.gen[Fruit]
        }
        test("decode from custom adt") {
          val json =
            """
              |[
              |  {
              |    "Apple": {
              |      "poison": false
              |    }
              |  },
              |  {
              |    "Banana": {
              |      "curvature": 0.5
              |    }
              |  }
              |]
              |""".stripMargin

          val decoded = json.fromJson[List[Fruit]]
          assertTrue(decoded == Right(List(Apple(false), Banana(0.5))))
        } +
          test("roundtrip custom adt") {
            val fruits    = List(Apple(false), Banana(0.5))
            val json      = fruits.toJson
            val roundTrip = json.fromJson[List[Fruit]]
            assertTrue(roundTrip == Right(fruits))
          }
      }
    )
}
