scalaVersion := "2.13.8"
organization := "dev.zio"
name         := "zio-quickstart-graphql-webservice"

libraryDependencies ++= Seq(
  "dev.zio"               %% "zio"              % "2.0.15",
  "com.github.ghostdogpr" %% "caliban"          % "2.0.0",
  "com.github.ghostdogpr" %% "caliban-zio-http" % "2.0.0",
  "io.d11"                %% "zhttp"            % "2.0.0-RC10"
)
