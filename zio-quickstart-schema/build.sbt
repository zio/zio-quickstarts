scalaVersion := "2.13.12"
organization := "dev.zio"
name         := "zio-quickstart-restful-schema"


libraryDependencies ++= Seq(
  "dev.zio" %% "zio-schema"          % "1.4.1",
  "dev.zio" %% "zio-schema-zio-test" % "1.4.1",
  "dev.zio" %% "zio-schema-derivation" % "1.4.1",
  "org.scala-lang" % "scala-reflect"  % scalaVersion.value % "provided",
  "dev.zio" %% "zio-test"          % "2.1.9" % Test,
  "dev.zio" %% "zio-test-sbt"      % "2.1.9" % Test,
  "dev.zio" %% "zio-test-magnolia" % "2.1.9" % Test
)

resolvers ++= Resolver.sonatypeOssRepos("snapshots")