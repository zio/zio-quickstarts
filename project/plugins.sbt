val zioSbtVersion = "0.4.0-alpha.6+15-525bdf8e-SNAPSHOT"

//addSbtPlugin("dev.zio" % "zio-sbt-ecosystem" % zioSbtVersion)
addSbtPlugin("dev.zio" % "zio-sbt-website" % zioSbtVersion)
addSbtPlugin("dev.zio" % "zio-sbt-ci"      % zioSbtVersion)

addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.16")
addSbtPlugin("io.spray" % "sbt-revolver" % "0.10.0")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.11.1")

resolvers ++= Resolver.sonatypeOssRepos("public")
