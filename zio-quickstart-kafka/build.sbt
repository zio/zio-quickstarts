scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio"         % "2.0.13",
  "dev.zio" %% "zio-streams" % "2.0.13",
  "dev.zio" %% "zio-kafka"   % "2.5.0",
  "dev.zio" %% "zio-json"    % "0.6.2"
)
