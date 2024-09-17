package dev.zio.quickstart

import PrepareDataUtils._
import zio.stream.ZStream
import zio.test._
import zio.{Chunk, ZIO}


object StreamWithSchemaValidation {
  private val res = withFile(fileName)(file => ZIO.attempt(prepareData(file)))

  private val runStream = ZStream
    .fromIterableZIO(res)
    .map{ person =>
      val validatedRecord = Person.schema.validate(person)

      validatedRecord match {
        case Chunk() => Right(person)
        case Chunk(_) | Chunk(_,_) => Left(person)
      }
    }

  def getTotalNumberofPeopleAbove18: ZIO[Any, Throwable, Int] =
    runStream
      .runFold(0)((acc, x) =>
        x match {
          case Right(_) => acc + 1
          case Left(_) => acc
        }
      )

  def getTotalNumberofRecords: ZIO[Any, Throwable, Int] =
    runStream
      .runCount
      .map(_.toInt)

  def getPeopleBelow18: ZIO[Any, Throwable, Int] =
    for {
      peopleAbove18 <- getTotalNumberofPeopleAbove18
      totalNumber <- getTotalNumberofRecords
    } yield totalNumber - peopleAbove18


  def getFirstPersonAbove18: ZIO[Any, Throwable, List[Person]] =
    runStream
      .runFold(List.empty[Person]){
        case (resList, Right(person)) => resList :+ person
        case (resList, Left(_)) => resList
      }

  def getFirstPersonbelow18: ZIO[Any, Throwable, List[Person]] =
    runStream
      .runFold(List.empty[Person]){
        case (resList, Left(person)) => resList :+ person
        case (resList, Right(_)) => resList
      }

}


object SchemaValidatorSpec extends ZIOSpecDefault {

  object Answers {
    val firstFiveNamesOfPeopleAbove18: List[Person] = List(
      Person("Michela Rizzo-Traetta", 69),
      Person("Gianpaolo Nibali", 29),
      Person("Orlando Gradenigo", 54),
      Person("Pasqual Disdero-Verri", 95),
      Person("Alphons Amato", 61)
    )

    val firstFiveNamesOfPeoplebelow18 = List(
      Person("Rosalia Marini", 3),
      Person("Melina Respighi", 11),
      Person("Ferdinando Prodi", 5),
      Person("Gioffre Sagnelli", 14),
      Person("Sig.ra Vanessa Cremonesi", 16)
    )

  }
    def spec: Spec[Any, Throwable] = suite("ExampleSpec")(
        test("Get total number of people"){
            assertZIO(StreamWithSchemaValidation.getTotalNumberofRecords)(Assertion.equalTo(31))
        },
        test("Get people above the age of 18"){
            for {
                count <- StreamWithSchemaValidation.getTotalNumberofPeopleAbove18
            } yield assertTrue(count == 26)
        },
        test("First name in the stream"){
            for {
                persons <- StreamWithSchemaValidation.getFirstPersonAbove18
            } yield assertTrue(persons.head == Answers.firstFiveNamesOfPeopleAbove18.head)
        },
        test("First five names of persons above 18"){
            for {
                persons <- StreamWithSchemaValidation.getFirstPersonAbove18
            } yield assertTrue(persons.take(5) == Answers.firstFiveNamesOfPeopleAbove18)
        },
        test("First name of person below 18 is Rosalia Marina"){
            for {
                persons <- StreamWithSchemaValidation.getFirstPersonbelow18
                nameOfFirstPerson = persons.head.name
                mustBeRosalia = Answers.firstFiveNamesOfPeoplebelow18.head.name

            } yield assertTrue(nameOfFirstPerson == mustBeRosalia)
        }

  )
}
