scalaVersion := "2.13.13"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio"        % "2.0.19",
  "dev.zio" %% "zio-macros" % "2.0.19"
)

scalacOptions += "-Ymacro-annotations"
