libraryDependencies ++= {
  val AkkaVersion       = "2.5.4"
  val AkkaHttpVersion   = "10.0.10"
  val AkkaHttpCircle    = "1.18.0"
  val circeVersion      = "0.8.0"
  val scalaTestVersion  = "3.0.4"
  val logbackVersion    = "1.2.3"
  val influxVersion     = "2.7"

  Seq(
    "com.typesafe.akka" %% "akka-slf4j"         % AkkaVersion,
    "com.typesafe.akka" %% "akka-actor"         % AkkaVersion,
    "com.typesafe.akka" %% "akka-stream"        % AkkaVersion,
    "com.typesafe.akka" %% "akka-http"          % AkkaHttpVersion,

    "de.heikoseeberger" %% "akka-http-circe"    % AkkaHttpCircle,
    "io.circe"          %% "circe-core"         % circeVersion,
    "io.circe"          %% "circe-generic"      % circeVersion,
    "io.circe"          %% "circe-parser"       % circeVersion,

    "ch.qos.logback"    %  "logback-classic"    % logbackVersion,

    "org.influxdb"      %  "influxdb-java"      % influxVersion,

    "com.typesafe.akka" %% "akka-testkit"       % AkkaVersion % "test",
    "com.typesafe.akka" %% "akka-http-testkit"  % AkkaHttpVersion % "test",
    "org.scalatest"     %% "scalatest"          % scalaTestVersion % "test"
  )
}
