scalaVersion := "2.13.13"
Test / fork  := true

libraryDependencies ++= Seq(
  "org.slf4j"          % "slf4j-simple"                    % "2.0.13",
  "dev.zio"           %% "zio-sql"                         % "0.1.2",
  "dev.zio"           %% "zio-sql-postgres"                % "0.1.2",
  "org.postgresql"     % "postgresql"                      % "42.7.3" % Compile,
  "dev.zio"           %% "zio-test"                        % "2.0.22" % Test,
  "dev.zio"           %% "zio-test-sbt"                    % "2.0.22" % Test,
  "dev.zio"           %% "zio-test-junit"                  % "2.0.22" % Test,
  "org.testcontainers" % "testcontainers"                  % "1.19.7" % Test,
  "org.testcontainers" % "database-commons"                % "1.19.7" % Test,
  "org.testcontainers" % "postgresql"                      % "1.19.7" % Test,
  "org.testcontainers" % "jdbc"                            % "1.19.7" % Test,
  "com.dimafeng"      %% "testcontainers-scala-postgresql" % "0.41.3" % Test
)
