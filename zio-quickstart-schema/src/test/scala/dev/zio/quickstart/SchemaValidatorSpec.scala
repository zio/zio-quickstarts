package dev.zio.quickstart

import PrepareDataUtils._
import zio.stream.ZStream
import zio.test._
import zio.{Chunk, ZIO}


object StreamWithSchemaValidation {
  private val listOfNameAndAgeOfPersons = withFile(resourceName)(file => ZIO.attempt(getNameAndAgeOfPerson(file)))
  private val listOfEmailOfPersons = withFile(resourceName)(file => ZIO.attempt(getEmailOfPerson(file)))

  private val streamOfPersonWithNameAndAge = ZStream
    .fromIterableZIO(listOfNameAndAgeOfPersons)
    .map{ person =>
      val validatedRecord = Person.schema.validate(person)

      validatedRecord match {
        case Chunk() => Right(person)
        case Chunk(_) | Chunk(_,_) => Left(person)
      }
    }

  private val streamOfPersonWithEmail = ZStream
    .fromIterableZIO(listOfEmailOfPersons)
    .map{ person =>
      val validatedRecord = PersonWithEmail.schema.validate(person)

      validatedRecord match {
        case Chunk() => Right(person)
        case Chunk(_) | Chunk(_,_) => Left(person)
      }
    }

  def getTotalNumberofPeopleAbove18: ZIO[Any, Throwable, Int] =
    streamOfPersonWithNameAndAge
      .runFold(0)((acc, x) =>
        x match {
          case Right(_) => acc + 1
          case Left(_) => acc
        }
      )

  def getTotalNumberofRecords: ZIO[Any, Throwable, Int] =
    streamOfPersonWithNameAndAge
      .runCount
      .map(_.toInt)

  def getPeopleBelow18: ZIO[Any, Throwable, Int] =
    for {
      peopleAbove18 <- getTotalNumberofPeopleAbove18
      totalNumber <- getTotalNumberofRecords
    } yield totalNumber - peopleAbove18

  def getFirstPersonAbove18: ZIO[Any, Throwable, List[Person]] =
    streamOfPersonWithNameAndAge
      .runFold(List.empty[Person]){
        case (resList, Right(person)) => resList :+ person
        case (resList, Left(_)) => resList
      }

  def getFirstPersonBelow18: ZIO[Any, Throwable, List[Person]] =
    streamOfPersonWithNameAndAge
      .runFold(List.empty[Person]){
        case (resList, Left(person)) => resList :+ person
        case (resList, Right(_)) => resList
      }

  def validEmailsTotal: ZIO[Any, Throwable, Int] =
    streamOfPersonWithEmail
      .runFold(0)((accum, res) => {
        res match {
          case Left(_) => accum
          case Right(_) => accum + 1
        }
      })

  def invalidEmailsTotal: ZIO[Any, Throwable, Int] = {
    for {
      res <- streamOfPersonWithEmail.runCollect
       total = res.size
      validEmailsTotal <- validEmailsTotal
    } yield total - validEmailsTotal
  }

  def listOfValidEmail = {
    for {
      list <- streamOfPersonWithEmail.runFold(List.empty[String])((list, res) => {
        res match {
          case Left(_) => list
          case Right(elem) => list :+ elem.email
        }
      })
    } yield list
  }

}


object ExamplesSpec extends ZIOSpecDefault {
  object PersonsData {
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

    val listOfValidEmails =  List(
      "annamaria26@example.org",
      "rosaria88@example.com",
      "stefano58@example.net",
      "federica81@example.org",
      "francesca93@example.org",
      "giulio81@example.net",
      "eleanora72@example.org",
      "imelda80@example.net",
      "virginia96@example.net"
    )

  }

  def spec: Spec[Any, Throwable] = suite("ExampleSpec")(
    test("Total number of people is 31"){
      assertZIO(StreamWithSchemaValidation.getTotalNumberofRecords)(Assertion.equalTo(31))
    },
    test("Number of persons above the age 18 is 26"){
      for {
        count <- StreamWithSchemaValidation.getTotalNumberofPeopleAbove18
      } yield assertTrue(count == 26)
    },
    test("Number of persons below the age 18 is 5"){
      for {
        count <- StreamWithSchemaValidation.getPeopleBelow18
      } yield assertTrue(count == 5)
    },
    test("First name in the stream of persons Above 18 is Michela Rizzo-Traetta"){
      for {
        persons <- StreamWithSchemaValidation.getFirstPersonAbove18
      } yield assertTrue(persons.head == PersonsData.firstFiveNamesOfPeopleAbove18.head)
    },
    test("First five names of persons above 18"){
      for {
        persons <- StreamWithSchemaValidation.getFirstPersonAbove18
      } yield assertTrue(persons.take(5) == PersonsData.firstFiveNamesOfPeopleAbove18)
    },
    test("First name of person below 18 is Rosalia Marina"){
      for {
        persons <- StreamWithSchemaValidation.getFirstPersonBelow18
        nameOfFirstPerson = persons.head.name
        mustBeRosalia = PersonsData.firstFiveNamesOfPeoplebelow18.head.name

      } yield assertTrue(nameOfFirstPerson == mustBeRosalia)
    },
    test("Total number of valid emails ( valid emails must have numbers before the '@'"){
      for {
        total <- StreamWithSchemaValidation.validEmailsTotal
      } yield assertTrue(total == 9)
    },
    test("Total number of invalid emails"){
      for {
        total <- StreamWithSchemaValidation.invalidEmailsTotal
      } yield assertTrue(total == 22)
    },
    test("first valid email is annamaria26@example.org"){
      for {
        emails <- StreamWithSchemaValidation.listOfValidEmail
        firstEmail = emails.head
      } yield assertTrue(firstEmail == PersonsData.listOfValidEmails.head)
    },
    test("valid emails list must match the one provided"){
      for {
        emails <- StreamWithSchemaValidation.listOfValidEmail

      } yield assertTrue(emails == PersonsData.listOfValidEmails, emails.size == 9)
    }
  )
}
