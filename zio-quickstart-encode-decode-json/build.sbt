scalaVersion := "2.13.8"
Test / fork  := true

libraryDependencies += "dev.zio" %% "zio"      % "2.0.18" % "test"
libraryDependencies += "dev.zio" %% "zio-test" % "2.0.17"
libraryDependencies += "dev.zio" %% "zio-json" % "0.6.2"
