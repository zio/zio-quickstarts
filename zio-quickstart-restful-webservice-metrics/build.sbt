scalaVersion := "2.13.8"
organization := "dev.zio"
name         := "zio-quickstart-restful-webservice"

libraryDependencies ++= Seq(
  "dev.zio"       %% "zio"                    % "2.0.18",
  "dev.zio"       %% "zio-metrics-connectors" % "2.0.8",
  "dev.zio"       %% "zio-json"               % "0.6.2",
  "dev.zio"       %% "zio-http"               % "3.0.0-RC2",
  "io.getquill"   %% "quill-zio"              % "4.6.0",
  "io.getquill"   %% "quill-jdbc-zio"         % "4.6.0",
  "com.h2database" % "h2"                     % "2.2.224"
)
