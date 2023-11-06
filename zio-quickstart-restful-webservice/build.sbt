scalaVersion := "3.3.1"

libraryDependencies ++= Seq(
  "dev.zio"       %% "zio"            % "2.0.19",
  "dev.zio"       %% "zio-json"       % "0.6.2",
  "dev.zio"       %% "zio-http"       % "3.0.0-RC2",
  "io.getquill"   %% "quill-zio"      % "4.7.0",
  "io.getquill"   %% "quill-jdbc-zio" % "4.7.0",
  "com.h2database" % "h2"             % "2.2.224"
)
