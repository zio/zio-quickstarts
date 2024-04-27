import zio._
import zio.test.assertTrue
import zio.prelude._
import zio.prelude.newtypes.Max
import zio.test.{Spec, TestEnvironment}
import zio.test.junit.JUnitRunnableSpec

object AssociativeSpec extends JUnitRunnableSpec {
  object Topic extends Newtype[String]
  type Topic = Topic.Type

  object Votes extends Subtype[Int] {
    implicit val Associative: Associative[Votes] = new Associative[Votes] {
      override def combine(l: => Votes, r: => Votes): Votes = Votes(l + r)
    }
  }
  type Votes = Votes.Type

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Associative")(
      test("combine custom class") {

        case class VoteMap(wrapped: Map[Topic, Votes])
        object VoteMap {
          implicit val Associative: Associative[VoteMap] =
            new Associative[VoteMap] {
              override def combine(l: => VoteMap, r: => VoteMap): VoteMap =
                VoteMap(l.wrapped <> r.wrapped)
            }
        }

        val vm1 = VoteMap(
          Map(Topic("newType") -> Votes(3), Topic("associative") -> Votes(1))
        )
        val vm2 = VoteMap(
          Map(Topic("associative") -> Votes(6), Topic("prelude") -> Votes(2))
        )
        val resultVm =
          VoteMap(
            Map(
              Topic("newType")     -> Votes(3),
              Topic("associative") -> Votes(7),
              Topic("prelude")     -> Votes(2)
            )
          )
        assertTrue(vm1 <> vm2 == resultVm)
      },
      test("combine as max using Max newtype") {
        val rawValues                = Seq(100, 262, 131, 66)
        val maxValues: Seq[Max[Int]] = Max.wrapAll(rawValues)
        assertTrue(maxValues.reduce(_ <> _) === rawValues.max)
      }
    )
}
