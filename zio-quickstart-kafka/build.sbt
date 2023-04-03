val `zio-quickstart-kafka` =
  project.settings(
    stdSettings(),
    enableZIO(enableStreaming = true)
  )

libraryDependencies ++= Seq(
  "dev.zio" %% "zio-kafka" % "2.0.0-M3",
  "dev.zio" %% "zio-json"  % "0.3.0-RC7"
)
