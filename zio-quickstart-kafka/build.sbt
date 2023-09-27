val `zio-quickstart-kafka` =
  project.settings(
    stdSettings(),
    enableZIO(enableStreaming = true)
  )

libraryDependencies ++= Seq(
  "dev.zio" %% "zio-kafka" % "2.4.3",
  "dev.zio" %% "zio-json"  % "0.6.2"
)
