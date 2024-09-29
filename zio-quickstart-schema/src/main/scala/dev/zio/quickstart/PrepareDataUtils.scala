package dev.zio.quickstart

import zio._

import java.time.LocalDate
import java.io.{File, FileNotFoundException, IOException}
import scala.io.{BufferedSource, Source}
import zio.schema

object PrepareDataUtils {

  val resourceName = "incoming_data.txt"

  def openFile(name: String): IO[IOException, BufferedSource] =
    ZIO.attemptBlockingIO(Source.fromResource(name))

  def closeFile(bufferedSourceFile: BufferedSource): ZIO[Any, Nothing, Unit] =
    ZIO.succeedBlocking(bufferedSourceFile.close())

  def withFile[A](name: String)(useFile: BufferedSource => Task[A]): Task[A] =
    ZIO.acquireReleaseWith(openFile(name))(closeFile)(useFile)
  def getNameAndAgeOfPerson(
      bufferedSourceFile: BufferedSource
  ): List[Person] = {

    def getPerson: Iterator[Person] = for {
      line <- bufferedSourceFile
        .getLines()
        .filter(incomingString => !incomingString.contains("Name"))
      list   = line.split(",")
      dob    = list.reverse.head
      name   = list.head
      age    = getAge(dob)
      person = Person(name, age)
    } yield person

    getPerson.toList

  }

  def getEmailOfPerson(
      bufferedSourceFile: BufferedSource
  ): List[PersonWithEmail] = {

    def getPersonWithEmail: Iterator[PersonWithEmail] = for {
      line <- bufferedSourceFile
        .getLines()
        .filter(incomingString => !incomingString.contains("Name"))
      arr   = line.split(",")
      emaii = arr(1)

      personWithEmail = PersonWithEmail(emaii)
    } yield personWithEmail

    getPersonWithEmail.toList

  }

  def getAge(dob: String): Int = {
    val currYear = LocalDate.now().getYear
    currYear - LocalDate.parse(dob).getYear
  }

}
