import zio._
import zio.test.assertTrue
import zio.prelude._
import zio.prelude.newtypes._
import zio.test.junit.JUnitRunnableSpec

object NewTypeSpec extends JUnitRunnableSpec {
  override def spec = suite("Newtype")(
    test("natural") {
      // new types to increase the type safety without compromising performance or ergonomics
      // Natural new type is an Int type with assertion on greater or equal to 0
      val five: Validation[String, Int]           = Natural.make(5)
      val notNatural: Validation[String, Natural] = Natural.make(-2)

      assertTrue(five.toOption.contains(5))
      assertTrue(
        notNatural == Validation.failNonEmptyChunk(
          NonEmptyChunk("-2 did not satisfy greaterThanOrEqualTo(0)")
        )
      )
      assertTrue(Natural.zero - Natural.one == -1) // unsafe manipulations
      assertTrue(
        Natural.minus(Natural.zero, Natural.one) == Natural.zero
      ) // safe manipulations
    },
    test("custom assertion") {
      // you can define your own value type with assertion
      object MyType extends Subtype[String] {
        override def assertion: QuotedAssertion[String] = (value: String) =>
          Either.cond(
            value.startsWith("!"),
            value,
            AssertionError.failure(s"must start with exclamation mark")
          )
      }

      val valid: Validation[String, String]    = MyType.make("!Hello!")
      val notValid: Validation[String, String] = MyType.make("NotValidString")

      assertTrue(valid.toOption.contains("!Hello!"))
      assertTrue(
        notValid == Validation.failNonEmptyChunk(
          NonEmptyChunk.single(
            "NotValidString did not satisfy must start with exclamation mark"
          )
        )
      )
    }
  )
}
