package dev.zio.quickstart.models

sealed trait Role

object Role {
  case object SoftwareDeveloper extends Role

  case object SiteReliabilityEngineer extends Role

  case object DevOps extends Role
}
