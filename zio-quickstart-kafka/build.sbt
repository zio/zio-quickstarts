val `zio-quickstart-kafka` =
  project.settings(
    stdSettings(),
    enableZIO(enableStreaming = true)
  )

libraryDependencies ++= Seq(
  "dev.zio" %% "zio-kafka" % "2.3.2",
  "dev.zio" %% "zio-json"  % "0.5.0"
)
