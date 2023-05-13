scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  "dev.zio"       %% "zio"                 % "2.0.13",
  "dev.zio"       %% "zio-json"            % "0.5.0",
  "dev.zio"       %% "zio-http"            % "0.0.5",
  "io.getquill"   %% "quill-zio"           % "4.6.0",
  "io.getquill"   %% "quill-jdbc-zio"      % "4.6.0",
  "com.h2database" % "h2"                  % "2.1.214",
  "dev.zio"       %% "zio-config"          % "4.0.0-RC16",
  "dev.zio"       %% "zio-config-typesafe" % "4.0.0-RC16",
  "dev.zio"       %% "zio-config-magnolia" % "4.0.0-RC16"
)

resolvers += Resolver.sonatypeRepo("public")
