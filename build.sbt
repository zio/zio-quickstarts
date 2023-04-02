organization := "dev.zio"

lazy val `zio-quickstart-restful-webservice` =
  project
    .in(
      file("zio-quickstart-restful-webservice")
    )
    .settings(
      name         := "zio-quickstart-restful-webservice",
      scalaVersion := "3.1.3",
      libraryDependencies ++= Seq(
        "dev.zio"       %% "zio"            % "2.0.1",
        "dev.zio"       %% "zio-json"       % "0.3.0-RC11",
        "io.d11"        %% "zhttp"          % "2.0.0-RC10",
        "io.getquill"   %% "quill-zio"      % "4.3.0",
        "io.getquill"   %% "quill-jdbc-zio" % "4.3.0",
        "com.h2database" % "h2"             % "2.1.214"
      )
    )

lazy val `zio-quickstart-restful-webservice-configurable-app` = project
