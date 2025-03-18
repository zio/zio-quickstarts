val zioSbtVersion = "0.4.0-alpha.30"

addSbtPlugin("dev.zio" % "zio-sbt-ecosystem" % zioSbtVersion)
addSbtPlugin("dev.zio" % "zio-sbt-website"   % zioSbtVersion)
addSbtPlugin("dev.zio" % "zio-sbt-ci"        % zioSbtVersion)

addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.11.1")
addSbtPlugin("io.spray"       % "sbt-revolver"        % "0.10.0")
addSbtPlugin("ch.epfl.scala"  % "sbt-scalafix"        % "0.14.0")

resolvers ++= Resolver.sonatypeOssRepos("public")
