scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  "dev.zio"       %% "zio"            % "2.0.13",
  "dev.zio"       %% "zio-json"       % "0.5.0",
  "io.d11"        %% "zhttp"          % "2.0.0-RC9",
  "io.getquill"   %% "quill-zio"      % "3.17.0-RC2",
  "io.getquill"   %% "quill-jdbc-zio" % "3.17.0-RC2",
  "com.h2database" % "h2"             % "2.1.214"
)
