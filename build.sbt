scalaVersion := "3.1.2"
organization := "dev.zio"
name         := "zio-quickstart-restful-webservice"

libraryDependencies ++= Seq(
  "dev.zio"       %% "zio"            % "2.0.0",
  "dev.zio"       %% "zio-json"       % "0.3.0-RC10",
  "io.d11"        %% "zhttp"        % "2.0.0-RC10",
  "io.getquill"   %% "quill-zio"    % "4.2.0",
  "io.getquill"   %% "quill-jdbc-zio" % "4.2.0",
  "com.h2database" % "h2"             % "2.1.214"
)
