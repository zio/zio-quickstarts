package dev.zio.quickstart

import zio._
import zio.schema._
import zio.schema.annotation.validate
import zio.schema.validation.Validation
import zio.stream.ZStream


object SchemaValidator extends ZIOAppDefault {

  import PrepareDataUtils._

  val res = withFile(resourceName)(file => ZIO.attempt(getNameAndAgeOfPerson(file)))

  val runStream = ZStream
    .fromIterableZIO(res)
    .map{ person =>
      Person.schema.validate(person) match {
        case Chunk() => Right(person)
        case Chunk(_) | Chunk(_, _) => Left(person)
      }

    }

  val listOfValidPersons =  runStream
    .runFold((List.empty[Person], List.empty[String])) {
      case ((valid, invalid), Right(person)) => (valid :+ person, invalid)  // Collect valid persons
      case ((valid, invalid), Left(error))   => (valid, invalid :+ error.toString)   // Collect errors
    }


  def run=
     program


  val program =
    for {
      count <-  runStream.runFold(0)((accum, _) => accum + 1)
      c <- listOfValidPersons
      _     <- Console.printLine(s"Total count: ${c._2.size}")
    } yield ()

}
