scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  "dev.zio"       %% "zio"               % "2.0.18",
  "dev.zio"       %% "zio-json"          % "0.6.2",
  "dev.zio"       %% "zio-http"          % "3.0.0-RC2",
  "io.getquill"   %% "quill-zio"         % "4.7.0",
  "io.getquill"   %% "quill-jdbc-zio"    % "4.7.0",
  "com.h2database" % "h2"                % "2.2.224",
  "dev.zio"       %% "zio-logging"       % "2.1.15",
  "dev.zio"       %% "zio-logging-slf4j" % "2.1.14",
  "org.slf4j"      % "slf4j-simple"      % "2.0.9"
)
