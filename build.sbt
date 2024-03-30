val Http4sVersion = "0.23.26"
val CirceVersion = "0.14.6"
val MunitVersion = "0.7.29"
val LogbackVersion = "1.5.3"
val MunitCatsEffectVersion = "1.0.7"

lazy val root = (project in file("."))
  .settings(
    organization := "com.example",
    name := "quickstart",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.13",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-ember-server" % Http4sVersion,
      "org.http4s"      %% "http4s-ember-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "org.scalameta"   %% "munit"               % MunitVersion           % Test,
      "org.typelevel"   %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test,
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion         % Runtime,
      "org.scalameta"   %  "svm-subs"            % "101.0.0",
      "org.postgresql" % "postgresql" % "42.3.0",
      "org.tpolecat" %% "doobie-h2" % "1.0.0-RC4", // H2 driver 1.4.200 + type mappings.
      "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC4", // HikariCP transactor.
      "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC4", // Postgres driver 42.6.0 + type mappings.
      "org.tpolecat" %% "doobie-specs2" % "1.0.0-RC4" % "test", // Specs2 support for typechecking statements.
      "org.tpolecat" %% "doobie-scalatest" % "1.0.0-RC4" % "test", // ScalaTest support for typechecking statements.
      "com.typesafe" % "config" % "1.4.2",
      "org.hsqldb" % "hsqldb" % "2.5.0",
      "org.flywaydb" % "flyway-core" % "9.16.3",
      "com.github.pureconfig" %% "pureconfig" % "0.17.6",
      "com.h2database" % "h2" % "2.2.224",
//      "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.3",
      ),
    Compile / run / fork := true,
      resolvers += Resolver.mavenCentral,
      addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.3" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
    assembly / assemblyMergeStrategy := {
      case "module-info.class" => MergeStrategy.discard
      case x => (assembly / assemblyMergeStrategy).value.apply(x)
    }
  )
