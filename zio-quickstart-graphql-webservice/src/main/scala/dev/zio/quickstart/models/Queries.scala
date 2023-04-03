package dev.zio.quickstart.models

import caliban.schema.Annotations.GQLDescription

case class Queries(
    @GQLDescription("Return all employees with specific role")
    employees: EmployeesArgs => List[Employee],
    @GQLDescription("Find an employee by its name")
    employee: EmployeeArgs => Option[Employee]
)
