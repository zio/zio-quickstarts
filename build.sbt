scalaVersion := "2.13.8"
organization := "dev.zio.quickstart"
name := "zio-quickstart-hello-world"

libraryDependencies += "dev.zio" %% "zio" % "2.0.0-RC5"
libraryDependencies += "dev.zio" %% "zio-json" % "0.3.0-RC7"
libraryDependencies += "io.d11" %% "zhttp" % "2.0.0-RC7"
libraryDependencies += "io.d11" %% "zhttp-test" % "2.0.0-RC7" % Test
