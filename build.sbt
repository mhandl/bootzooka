import java.text.SimpleDateFormat
import java.util.Date

import sbt._
import Keys._

import scala.util.Try
import scalariform.formatter.preferences._
import complete.DefaultParsers._

val slf4jVersion = "1.7.21"
val logBackVersion = "1.1.7"
val scalaLoggingVersion = "3.5.0"
val slickVersion = "3.1.1"
val seleniumVersion = "3.0.1"
val circeVersion = "0.6.1"
val akkaVersion = "2.4.14"
val akkaHttpVersion = "10.0.0"
val phantomVersion = "1.29.6"

val slf4jApi = "org.slf4j" % "slf4j-api" % slf4jVersion
val logBackClassic = "ch.qos.logback" % "logback-classic" % logBackVersion
val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
val loggingStack = Seq(slf4jApi, logBackClassic, scalaLogging)


val circeCore = "io.circe" %% "circe-core" % circeVersion
val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
val circeJawn = "io.circe" %% "circe-jawn" % circeVersion
val circeAkkaHttp =  "de.heikoseeberger" %% "akka-http-circe" % "1.11.0"
val javaxMailSun = "com.sun.mail" % "javax.mail" % "1.5.5"

val circe = Seq(circeCore, circeGeneric, circeJawn)

val slick = "com.typesafe.slick" %% "slick" % slickVersion
val slickHikari = "com.typesafe.slick" %% "slick-hikaricp" % slickVersion
val h2 = "com.h2database" % "h2" % "1.3.176" //watch out! 1.4.190 is beta
val postgres = "org.postgresql" % "postgresql" % "9.4.1208"
val flyway = "org.flywaydb" % "flyway-core" % "4.0"
val slickStack = Seq(slick, h2, postgres, slickHikari, flyway)

val typesafeConfig = "com.typesafe" % "config" % "1.3.1"


val scalatest = "org.scalatest" %% "scalatest" % "3.0.1" % "test"
val unitTestingStack = Seq(scalatest)

val seleniumJava = "org.seleniumhq.selenium" % "selenium-java" % seleniumVersion % "test"
val seleniumFirefox = "org.seleniumhq.selenium" % "selenium-firefox-driver" % seleniumVersion % "test"
val seleniumStack = Seq(seleniumJava, seleniumFirefox)

val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
val akkaHttpSession = "com.softwaremill.akka-http-session" %% "core" % "0.3.0"
val akkaClusterTools = "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion
val akkaPersistence = "com.typesafe.akka" %% "akka-persistence" % akkaVersion
val cassandraPersistence = "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.21"

val akkaHttpCore = "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion
val akkaHttpExperimental = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test"
val inMemoryPersistence =  "com.github.dnvriend" %% "akka-persistence-inmemory" % "1.3.17" % "test"
val akkaStack = Seq(akkaHttp, akkaHttpTestkit, akkaHttpSession, akkaClusterTools, akkaPersistence,
  cassandraPersistence, inMemoryPersistence)

val phantom  = "com.websudos" %% "phantom-dsl" % phantomVersion
val utilTest = "com.outworkers" % "util-testing_2.11" % "0.26.4" % "test"
val phantomStack = Seq(phantom, utilTest)

val constructr =     "de.heikoseeberger" %% "constructr"                   % "0.15.0"
val constructrEtcd = "de.heikoseeberger" %% "constructr-coordination-etcd" % "0.15.0"
val constructrStack = Seq(constructr, constructrEtcd)

val ammonite = Seq("com.lihaoyi" % "ammonite" % "0.8.0" % "test" cross CrossVersion.full)

val scalaXml = Seq("org.scala-lang.modules" %% "scala-xml" % "1.0.6")

val mockito = Seq("org.mockito" % "mockito-all" % "1.10.19" % "test")

val exclude = Seq(
  ("org.scala-lang" % "scala-compiler" % "2.11.8").exclude("org.scala-lang.modules", s"scala-xml*"),
  ("org.scala-lang" % "scala-compiler" % "2.11.8" % "test").exclude("org.scala-lang.modules", s"scala-xml*"))

val jug = Seq("com.fasterxml.uuid" % "java-uuid-generator" % "3.1.4")


val commonDependencies = unitTestingStack ++ loggingStack

lazy val updateNpm = taskKey[Unit]("Update npm")
lazy val npmTask = inputKey[Unit]("Run npm with arguments")

resolvers in ThisBuild += Resolver.bintrayRepo("websudos", "oss-releases")

lazy val commonSettings = SbtScalariform.scalariformSettings ++ Seq(
  scalariformPreferences := scalariformPreferences.value
    .setPreference(DoubleIndentClassDeclaration, true)
    .setPreference(PreserveSpaceBeforeArguments, true)
    .setPreference(CompactControlReadability, true)
    .setPreference(SpacesAroundMultiImports, false),
  organization := "com.softwaremill",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-unchecked", "-deprecation"),
  libraryDependencies ++= commonDependencies,
  updateNpm := {
    println("Updating npm dependencies")
    haltOnCmdResultError(Process("npm install", baseDirectory.value / ".." / "ui") !)
  },
  npmTask := {
    val taskName = spaceDelimited("<arg>").parsed.mkString(" ")
    updateNpm.value
    val localNpmCommand = "npm " + taskName
    def buildWebpack() = {
      Process(localNpmCommand, baseDirectory.value / ".." / "ui").!
    }
    println("Building with Webpack : " + taskName)
    haltOnCmdResultError(buildWebpack())
  }
)

def haltOnCmdResultError(result: Int) {
  if (result != 0) {
    throw new Exception("Build failed.")
  }
}

lazy val rootProject = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "bootzooka",
    herokuFatJar in Compile := Some((assemblyOutputPath in backend in assembly).value),
    deployHeroku in Compile := ((deployHeroku in Compile) dependsOn (assembly in backend)).value
  )
  .aggregate(backend, ui)


lazy val backend: Project = (project in file("backend"))
  .enablePlugins(
    BuildInfoPlugin,
//    JavaAppPackaging,
    DockerPlugin)
  .settings(commonSettings)
  .settings(Revolver.settings)
  .settings(
    libraryDependencies ++= slickStack ++ akkaStack ++ circe ++ exclude ++ scalaXml ++ mockito ++ jug ++ Seq(javaxMailSun,typesafeConfig) ++ constructrStack ++ phantomStack ++ ammonite,
    buildInfoPackage := "com.softwaremill.bootzooka.version",
    buildInfoObject := "BuildInfo",
    buildInfoKeys := Seq[BuildInfoKey](
      BuildInfoKey.action("buildDate")(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())),
      // if the build is done outside of a git repository, we still want it to succeed
      BuildInfoKey.action("buildSha")(Try(Process("git rev-parse HEAD").!!.stripLineEnd).getOrElse("?"))),
    compile in Compile := {
      val compilationResult = (compile in Compile).value
      IO.touch(target.value / "compilationFinished")

      compilationResult
    },
    mainClass in Compile := Some("com.softwaremill.bootzooka.Main"),
    // We need to include the whole webapp, hence replacing the resource directory
    unmanagedResourceDirectories in Compile := {
      (unmanagedResourceDirectories in Compile).value ++ List(baseDirectory.value.getParentFile / ui.base.getName / "dist")
    },
    assemblyJarName in assembly := "bootzooka.jar",
    assembly := assembly.dependsOn(npmTask.toTask(" run build")).value
  )

lazy val ui = (project in file("ui"))
  .settings(commonSettings: _*)
  .settings(test in Test := (test in Test).dependsOn(npmTask.toTask(" run test")).value)

lazy val uiTests = (project in file("ui-tests"))
  .settings(commonSettings: _*)
  .settings(
    parallelExecution := false,
    libraryDependencies ++= seleniumStack,
    test in Test := (test in Test).dependsOn(npmTask.toTask(" run build")).value
  ) dependsOn backend

RenameProject.settings
