scalaVersion := "2.13.8"
Test / fork  := true

libraryDependencies += "dev.zio" %% "zio"      % "2.0.19" % "test"
libraryDependencies += "dev.zio" %% "zio-test" % "2.0.19"
libraryDependencies += "dev.zio" %% "zio-json" % "0.6.2"
