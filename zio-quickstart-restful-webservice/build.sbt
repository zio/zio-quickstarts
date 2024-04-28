scalaVersion := "3.3.1"
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  "dev.zio"       %% "zio"            % "2.0.22",
  "dev.zio"       %% "zio-json"       % "0.6.2",
  "dev.zio"       %% "zio-http"       % "3.0.0-RC6+36-d283e073-SNAPSHOT",
  "io.getquill"   %% "quill-zio"      % "4.7.0",
  "io.getquill"   %% "quill-jdbc-zio" % "4.7.0",
  "com.h2database" % "h2"             % "2.2.224"
)
