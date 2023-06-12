scalaVersion := "2.13.8"
organization := "dev.zio"
name         := "zio-quickstart-restful-webservice"

libraryDependencies ++= Seq(
  "dev.zio"       %% "zio"                    % "2.0.15",
  "dev.zio"       %% "zio-metrics-connectors" % "2.0.8",
  "dev.zio"       %% "zio-json"               % "0.5.0",
  "dev.zio"       %% "zio-http"               % "0.0.3",
  "io.getquill"   %% "quill-zio"              % "3.17.0-RC3",
  "io.getquill"   %% "quill-jdbc-zio"         % "3.17.0-RC3",
  "com.h2database" % "h2"                     % "2.1.214"
)
