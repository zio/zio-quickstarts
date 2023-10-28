val `zio-quickstart-kafka` =
  project.settings(
    stdSettings(),
    enableZIO(enableStreaming = true)
  )

libraryDependencies ++= Seq(
  "dev.zio" %% "zio-kafka" % "2.6.0",
  "dev.zio" %% "zio-json"  % "0.6.2"
)
