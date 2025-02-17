ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

val zioVersion               = "2.0.0-RC5"
val zioJsonVersion           = "0.3.0-RC7"
val zioHttpVersion           = "2.0.0-RC7"
val zioQuillVersion          = "3.17.0-RC3"
val postgresVersion          = "42.3.4"
val flywayVersion            = "8.5.10"
val zioTestContainersVersion = "0.4.1"
val laminarVersion           = "0.14.2"
val animusVersion            = "0.1.12"

Global / onChangedBuildSource := ReloadOnSourceChanges

val sharedSettings = Seq(
  libraryDependencies ++= Seq(
    "dev.zio" %%% "zio-json" % zioJsonVersion
  )
)

lazy val root = (project in file("."))
  .aggregate(backend, frontend, shared)
  .settings(name := "pet-clinic")

lazy val backend = (project in file("backend"))
  .settings(
    name := "pet-clinic-backend",
    libraryDependencies ++= Seq(
      "dev.zio"               %% "zio"                               % zioVersion,
      "dev.zio"               %% "zio-test"                          % zioVersion     % Test,
      "dev.zio"               %% "zio-test-sbt"                      % zioVersion     % Test,
      "io.d11"                %% "zhttp"                             % zioHttpVersion,
      "io.d11"                %% "zhttp-test"                        % zioHttpVersion % Test,
      "io.getquill"           %% "quill-jdbc-zio"                    % zioQuillVersion,
      "org.postgresql"         % "postgresql"                        % postgresVersion,
      "org.flywaydb"           % "flyway-core"                       % flywayVersion,
      "io.github.scottweaver" %% "zio-2-0-testcontainers-postgresql" % zioTestContainersVersion,
      "io.github.scottweaver" %% "zio-2-0-db-migration-aspect"       % zioTestContainersVersion
    ),
    Test / fork := true,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
  .settings(sharedSettings)
  .enablePlugins(FlywayPlugin)
  .settings(
    flywayUrl      := "jdbc:postgresql://localhost:5432/olive",
    flywayUser     := "postgres",
    flywayPassword := ""
  )
  .dependsOn(shared)

lazy val frontend = (project in file("frontend"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "pet-clinic-frontend",
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= { _.withSourceMap(false) },
    libraryDependencies ++= Seq(
      "com.raquo"                     %%% "laminar"         % laminarVersion,
      "io.github.kitlangton"          %%% "animus"          % animusVersion,
      "com.raquo"                     %%% "waypoint"        % "0.5.0",
      "io.github.cquiroz"             %%% "scala-java-time" % "2.3.0",
      "com.softwaremill.sttp.client3" %%% "core"            % "3.6.1"
    )
  )
  .settings(sharedSettings)
  .dependsOn(shared)

lazy val shared = (project in file("shared"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaJSLinkerConfig ~= { _.withSourceMap(false) },
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) }
  )
  .settings(sharedSettings)
