import org.junit.runner.RunWith
import zio._
import zio.test._
import zio.test.junit.ZTestJUnitRunner

@RunWith(classOf[ZTestJUnitRunner])
class BarSpec extends ZIOSpecDefault {
  def spec = suite("BarSpec")(
    test("bar test") {
      for {
        _ <- ZIO.unit
      } yield assertCompletes
    }
  )
}
