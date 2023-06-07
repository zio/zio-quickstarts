scalaVersion := "3.3.0"

libraryDependencies ++= Seq(
  "dev.zio"       %% "zio"            % "2.0.15",
  "dev.zio"       %% "zio-json"       % "0.5.0",
  "io.d11"        %% "zhttp"          % "2.0.0-RC10",
  "io.getquill"   %% "quill-zio"      % "4.3.0",
  "io.getquill"   %% "quill-jdbc-zio" % "4.3.0",
  "com.h2database" % "h2"             % "2.1.214"
)
