scalaVersion := "2.13.8"
Test / fork  := true

libraryDependencies += "dev.zio" %% "zio"      % "2.0.0-RC6" % "test"
libraryDependencies += "dev.zio" %% "zio-test" % "2.0.0-RC6"
libraryDependencies += "dev.zio" %% "zio-json" % "0.3.0-RC8"
