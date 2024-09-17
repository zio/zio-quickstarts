package dev.zio.quickstart

import zio._

import java.time.LocalDate
import java.io.{File, FileNotFoundException, IOException}
import scala.io.{BufferedSource, Source}
import zio.schema

object PrepareDataUtils {

  val homeDirectory = java.lang.System.getProperty("user.home")
  val fileName =  homeDirectory + "/Desktop/centre/scala/zio-quickstarts/zio-quickstart-schema/src/main/resources/incoming_data.txt"


  def openFile(name: String): IO[IOException, BufferedSource] =
    ZIO.attemptBlockingIO(Source.fromFile(name))

  def closeFile(bufferedSourceFile: BufferedSource): ZIO[Any, Nothing, Unit] =
    ZIO.succeedBlocking(bufferedSourceFile.close())

  def withFile[A](name: String)(useFile: BufferedSource => Task[A]): Task[A] =
    ZIO.acquireReleaseWith(openFile(name))(closeFile)(useFile)

  def prepareData(bufferedSourceFile: BufferedSource): List[Person] = {

    def getPerson: Iterator[Person] = for {
        line <- bufferedSourceFile.getLines().filter(incomingString => !incomingString.contains("Name"))
        arr  = line.split(",")
        dob = arr.reverse.head
        name = arr.head
        age  = getAge(dob)
        person = Person(name, age)
    } yield person

    getPerson.toList

  }

  def getAge(dob: String): Int = {
    val currYear = LocalDate.now().getYear
    currYear - LocalDate.parse(dob).getYear
  }

} 


