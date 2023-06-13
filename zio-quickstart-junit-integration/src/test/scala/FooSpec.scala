import zio._
import zio.test._
import zio.test.junit.JUnitRunnableSpec

object FooSpec extends JUnitRunnableSpec {
  def spec = suite("FooSpec")(
    test("foo test") {
      for {
        _ <- ZIO.unit
      } yield assertCompletes
    }
  )
}
