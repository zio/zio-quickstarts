package dev.zio.quickstart

import caliban.GraphQL.graphQL
import caliban.{RootResolver, ZHttpAdapter}
import dev.zio.quickstart.models._
import zhttp.http._
import zhttp.service.Server
import zio.ZIOAppDefault

import scala.language.postfixOps

object MainApp extends ZIOAppDefault {

  private val employees = List(
    Employee("Alex", Role.DevOps),
    Employee("Maria", Role.SoftwareDeveloper),
    Employee("James", Role.SiteReliabilityEngineer),
    Employee("Peter", Role.SoftwareDeveloper),
    Employee("Julia", Role.SiteReliabilityEngineer),
    Employee("Roberta", Role.DevOps)
  )

  override def run =
    graphQL(
      RootResolver(
        Queries(
          args => employees.filter(e => args.role == e.role),
          args => employees.find(e => e.name == args.name)
        )
      )
    ).interpreter.flatMap(interpreter =>
      Server
        .start(
          port = 8088,
          http = Http.collectHttp { case _ -> !! / "api" / "graphql" =>
            ZHttpAdapter.makeHttpService(interpreter)
          }
        )
    )

}
