import Dependencies._

ThisBuild / version := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(nocomma {
    name := "Eval"
    libraryDependencies ++= Seq(
      scalaCompiler,
      verify % Test,
      ioProj % Test,
    )
    testFrameworks += new TestFramework("verify.runner.Framework")
    crossVersion := CrossVersion.full
    Test / fork := true
  })

ThisBuild / semanticdbEnabled := true
ThisBuild / scalaVersion := scala3
ThisBuild / organization := "com.eed3si9n.eval"
ThisBuild / organizationName := "eed3si9n"
ThisBuild / organizationHomepage := Some(url("http://eed3si9n.com/"))
ThisBuild / scmInfo := Some(
  ScmInfo(url("https://github.com/eed3si9n/eval"), "git@github.com:eed3si9n/eval.git")
)
ThisBuild / developers := List(
  Developer("eed3si9n", "Eugene Yokota", "@eed3si9n", url("https://github.com/eed3si9n")),
)
ThisBuild / description := "Eval evaluates Scala code"
ThisBuild / licenses := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/eed3si9n/eval"))
ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
