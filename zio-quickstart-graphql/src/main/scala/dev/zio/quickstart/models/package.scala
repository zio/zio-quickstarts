package dev.zio.quickstart

package object models {
  case class Employee(name: String, role: Role)

  case class EmployeesArgs(role: Role)

  case class EmployeeArgs(name: String)
}
