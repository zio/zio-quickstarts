scalaVersion := "2.13.8"
organization := "dev.zio"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio"               % "2.0.18",
  "dev.zio" %% "zio-test"          % "2.0.18" % Test,
  "dev.zio" %% "zio-test-sbt"      % "2.0.17" % Test,
  "dev.zio" %% "zio-test-junit"    % "2.0.17" % Test
)

libraryDependencies += "com.github.sbt" % "junit-interface" % "0.13.3" % Test
