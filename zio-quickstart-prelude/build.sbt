scalaVersion := "2.13.13"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio-prelude"    % "1.0.0-RC23",
  "dev.zio" %% "zio-test"       % "2.0.22" % Test,
  "dev.zio" %% "zio-test-sbt"   % "2.0.22" % Test,
  "dev.zio" %% "zio-test-junit" % "2.0.22" % Test
)
