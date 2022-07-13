scalaVersion := "2.13.8"
organization := "dev.zio"
name         := "zio-quickstart-restful-webservice"

libraryDependencies ++= Seq(
  "dev.zio"       %% "zio"            % "2.0.0-RC6",
  "dev.zio"       %% "zio-json"       % "0.3.0-RC8",
  "io.d11"        %% "zhttp"          % "2.0.0-RC9",
  "io.getquill"   %% "quill-zio"      % "3.17.0-RC2",
  "io.getquill"   %% "quill-jdbc-zio" % "3.17.0-RC2",
  "com.h2database" % "h2"             % "2.1.214"
)
