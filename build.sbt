val ScalaLTS = "3.3.5"
val ScalaNext = "3.6.4"
ThisBuild / organization := "io.github.marcinzh"
ThisBuild / version := "0.8.0"
ThisBuild / scalaVersion := ScalaLTS
ThisBuild / crossScalaVersions := Seq(ScalaLTS, ScalaNext)

ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Wnonunit-statement",
  "-Xfatal-warnings",
  Seq(
    "java.lang",
    "scala",
    "scala.Predef",
    "scala.util.chaining",
  ).mkString("-Yimports:", ",", "")
)

ThisBuild / scalacOptions += {
  if (VersionNumber(scalaVersion.value).matchesSemVer(SemanticSelector(">=3.4.0")))
    "-Xkind-projector:underscores"
  else
    "-Ykind-projector:underscores"
}
ThisBuild / publish / skip := (scalaVersion.value != ScalaLTS)


val Deps = {
  val jsonitter_v = "2.35.0"
  val tur_v = "0.112.0"
  object deps {
    val specs2_core = "org.specs2" %% "specs2-core" % "5.4.0" % "test"
    val turbolift_core = "io.github.marcinzh" %% "turbolift-core" % tur_v
    val turbolift_bindless = "io.github.marcinzh" %% "turbolift-bindless" % tur_v
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
    Deps.turbolift_core,
    Deps.undertow,
    Deps.jsoniter_core,
    Deps.jsoniter_macros,
  ))

lazy val examples = project
  .in(file("modules/examples"))
  .settings(name := "enterprise-examples")
  .settings(publish / skip := true)
  .settings(Compile / run / mainClass := Some("runner.Main"))
  .settings(libraryDependencies += Deps.turbolift_bindless)
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
ThisBuild / licenses := List("MIT" -> url("http://www.opensource.org/licenses/MIT"))
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
