organization := "dev.zio"

lazy val root =
  project.aggregate(
    `zio-quickstart-restful-webservice`,
    `zio-quickstart-restful-webservice-configurable-app`,
    `zio-quickstart-restful-webservice-custom-logger`,
    `zio-quickstart-restful-webservice-dockerize`,
    `zio-quickstart-restful-webservice-logging`,
    `zio-quickstart-restful-webservice-metrics`
  )

lazy val `zio-quickstart-restful-webservice`                  = project
lazy val `zio-quickstart-restful-webservice-configurable-app` = project
lazy val `zio-quickstart-restful-webservice-custom-logger`    = project
lazy val `zio-quickstart-restful-webservice-dockerize`        = project
lazy val `zio-quickstart-restful-webservice-logging`          = project
lazy val `zio-quickstart-restful-webservice-metrics`          = project
