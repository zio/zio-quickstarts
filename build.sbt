scalaVersion := "2.13.8"
organization := "dev.zio"
name         := "zio-quickstart-restful-webservice"
version      := "0.1.0"

libraryDependencies ++= Seq(
  "dev.zio"       %% "zio"            % "2.0.0-RC5",
  "dev.zio"       %% "zio-json"       % "0.3.0-RC7",
  "io.d11"        %% "zhttp"          % "2.0.0-RC7",
  "io.getquill"   %% "quill-zio"      % "3.17.0-RC3",
  "io.getquill"   %% "quill-jdbc-zio" % "3.17.0-RC3",
  "com.h2database" % "h2"             % "2.1.212"
)

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

dockerExposedPorts := Seq(8080)

dockerUsername := sys.props.get("docker.username")
dockerRepository := sys.props.get("docker.registry")