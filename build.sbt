ThisBuild / organization := "io.github.marcinzh"
ThisBuild / version := "0.3.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.3"
ThisBuild / crossScalaVersions := Seq(scalaVersion.value)

ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Wnonunit-statement",
  "-Xfatal-warnings",
  "-Ykind-projector:underscores",
  Seq(
    "java.lang",
    "scala",
    "scala.Predef",
    "scala.util.chaining",
  ).mkString("-Yimports:", ",", "")
)

val Deps = {
  val jsonitter_v = "2.23.2"
  object deps {
    val specs2_core = "org.specs2" %% "specs2-core" % "5.2.0" % "test"
    val turbolift = "io.github.marcinzh" %% "turbolift-core" % "0.79.0-SNAPSHOT"
    val betterFiles = ("com.github.pathikrit" %% "better-files" % "3.9.1").cross(CrossVersion.for3Use2_13)
    val undertow = "io.undertow" % "undertow-core" % "2.2.20.Final"
    val jsoniter_core ="com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % jsonitter_v
    val jsoniter_macros ="com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % jsonitter_v
  }
  deps
}

lazy val root = project
  .in(file("."))
  .settings(sourcesInBase := false)
  .settings(publish / skip := true)
  .aggregate(core, examples)

lazy val core = project
  .in(file("modules/core"))
  .settings(name := "enterprise-core")
  .settings(testSettings: _*)
  .settings(libraryDependencies ++= Seq(
    Deps.turbolift,
    Deps.undertow,
    Deps.jsoniter_core,
    Deps.jsoniter_macros,
  ))

lazy val examples = project
  .in(file("modules/examples"))
  .settings(name := "enterprise-examples")
  .settings(publish / skip := true)
  .dependsOn(core)

//=================================================

lazy val testSettings = Seq(
  libraryDependencies += Deps.specs2_core,
  Test / parallelExecution := false,
)

ThisBuild / watchBeforeCommand := Watch.clearScreen
ThisBuild / watchTriggeredMessage := Watch.clearScreenOnTrigger
ThisBuild / watchForceTriggerOnAnyChange := true

ThisBuild / description := "Serve HTTP using algebraic effects and handlers"
ThisBuild / organizationName := "marcinzh"
ThisBuild / homepage := Some(url("https://github.com/marcinzh/enterprise"))
ThisBuild / scmInfo := Some(ScmInfo(url("https://github.com/marcinzh/enterprise"), "scm:git@github.com:marcinzh/enterprise.git"))
ThisBuild / licenses := List("MIT" -> new URL("http://www.opensource.org/licenses/MIT"))
ThisBuild / versionScheme := Some("semver-spec")
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishMavenStyle := true
ThisBuild / publishTo := {
  val nexus = "https://s01.oss.sonatype.org/"
  isSnapshot.value match {
    case true => Some("snapshots" at nexus + "content/repositories/snapshots")
    case false => Some("releases" at nexus + "service/local/staging/deploy/maven2")
  }
}
ThisBuild / pomExtra := (
  <developers>
    <developer>
      <id>marcinzh</id>
      <name>Marcin Żebrowski</name>
      <url>https://github.com/marcinzh</url>
    </developer>
  </developers>
)
