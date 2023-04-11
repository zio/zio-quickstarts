scalaVersion := "2.13.8"
Test / fork  := true

libraryDependencies += "dev.zio" %% "zio"      % "2.0.12" % "test"
libraryDependencies += "dev.zio" %% "zio-test" % "2.0.11"
libraryDependencies += "dev.zio" %% "zio-json" % "0.5.0"
