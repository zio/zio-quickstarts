import zio._
import zio.prelude._
import zio.test.{Spec, TestEnvironment, assertTrue}
import zio.test.junit.JUnitRunnableSpec

object ValidationSpec extends JUnitRunnableSpec {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Validation")(
      test("validate class") {
        case class Person(name: String, age: Int)

        def validateName(name: String): Validation[String, String] =
          if (name.isEmpty) Validation.fail("Name was empty")
          else Validation.succeed(name)

        def validateAge(age: Int): Validation[String, Int] =
          Validation.fromPredicateWith(s"Age $age was less than zero")(age)(
            _ >= 0
          )

        def validatePerson(name: String, age: Int): Validation[String, Person] =
          Validation.validateWith(validateName(name), validateAge(age))(
            Person.apply
          )

        assertTrue(
          validatePerson("Grisha", 25).toOption.contains(Person("Grisha", 25))
        )
        assertTrue(
          validatePerson("", -5) == Validation.failNonEmptyChunk(
            NonEmptyChunk("Name was empty", "Age -5 was less than zero")
          )
        )
      },
      test("validate newtype") {
        object Name extends Subtype[String] {
          override def assertion = assert(!Assertion.isEmptyString)
        }
        type Name = Name.Type
        object Age extends Subtype[Int] {
          override def assertion = assert(Assertion.greaterThanOrEqualTo(0))
        }
        type Age = Age.Type

        case class Person(name: Name, age: Age)

        def validatePerson(name: String, age: Int) =
          Validation.validateWith(Name.make(name), Age.make(age))(Person.apply)

        assertTrue(
          validatePerson("Grisha", 25).toOption.contains(
            Person(Name("Grisha"), Age(25))
          )
        )
        assertTrue(
          validatePerson("", -5) == Validation.failNonEmptyChunk(
            NonEmptyChunk(
              " did not satisfy hasLength(notEqualTo(0))",
              "-5 did not satisfy greaterThanOrEqualTo(0)"
            )
          )
        )
      },
      test("chaining validations") {
        object Age extends Subtype[Int] {
          override def assertion = assert(Assertion.greaterThanOrEqualTo(0))
        }
        type Age = Age.Type

        def validateNonEmpty(
            s: String
        ): Validation[String, NonEmptyList[String]] =
          Validation.fromOptionWith(
            "String must contain at least one value divided by space character"
          )(NonEmptyList.fromIterableOption(s.split(" ")))

        def validateInt(s: String): Validation[String, Int] =
          Validation.fromOptionWith(s"String must be int like, but got $s")(
            s.toIntOption
          )

        def validateAge(i: Int): Validation[String, Age] = Age.make(i)

        def calculateResult(
            line: String
        ): Validation[String, NonEmptyList[Age]] =
          for {
            strAges <- validateNonEmpty(line)
            intAges <- Validation.validateAll(strAges.map(validateInt))
            ages    <- Validation.validateAll(intAges.map(validateAge))
          } yield ages

        val result1 = calculateResult("12 10 5")
        val result2 = calculateResult("12 -5 -2")
        val result3 = calculateResult("")
        val result4 = calculateResult("12 _f")

        assertTrue(
          result1.toOption.get === NonEmptyList(Age(12), Age(10), Age(5))
        )
        assertTrue(
          result2 == Validation.failNonEmptyChunk(
            NonEmptyChunk(
              "-5 did not satisfy greaterThanOrEqualTo(0)",
              "-2 did not satisfy greaterThanOrEqualTo(0)"
            )
          )
        )
        assertTrue(
          result3 == Validation.failNonEmptyChunk(
            NonEmptyChunk.single(
              "String must contain at least one value divided by space character"
            )
          )
        )
        assertTrue(
          result4 == Validation.failNonEmptyChunk(
            NonEmptyChunk.single("String must be int like, but got _f")
          )
        )
      }
    )
}
