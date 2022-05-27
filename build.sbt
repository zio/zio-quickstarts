scalaVersion := "2.13.8"
organization := "dev.zio"
name         := "zio-quickstart-restful-webservice"

libraryDependencies ++= Seq(
  "dev.zio"       %% "zio"                 % "2.0.0-RC5",
  "dev.zio"       %% "zio-json"            % "0.3.0-RC7",
  "io.d11"        %% "zhttp"               % "2.0.0-RC7",
  "io.getquill"   %% "quill-zio"           % "3.17.0-RC3",
  "io.getquill"   %% "quill-jdbc-zio"      % "3.17.0-RC3",
  "com.h2database" % "h2"                  % "2.1.212",
  "dev.zio"       %% "zio-config"          % "3.0.0-RC8",
  "dev.zio"       %% "zio-config-typesafe" % "3.0.0-RC8",
  "dev.zio"       %% "zio-config-magnolia" % "3.0.0-RC8"
)
