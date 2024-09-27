package dev.zio.quickstart

import com.dimafeng.testcontainers.PostgreSQLContainer
import zio._
import zio.schema.{DeriveSchema, Schema}
import zio.test.{Spec, assertTrue}

import java.time.LocalDate
import java.util.UUID

import org.testcontainers.utility.DockerImageName

import java.sql.DriverManager

object Pg extends JdbcRunnableSpec {

  def specLayered: Spec[Pg.JdbcEnvironment, Object] =
    suite("Checking") {
      test("er") {
        for {
          url <- ZIO.succeed(getContainer.username)
          _   <- ZIO.succeed("")
        } yield assertTrue(url == "ew")
      }
    }
}
