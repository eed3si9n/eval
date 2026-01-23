import Dependencies._

lazy val scalaVersions = Seq(
  // "3.6.4",
  // "3.5.2",
  // "3.4.3",
  // "3.3.5",
  "3.8.1"
)
ThisBuild / version := "0.3.2-SNAPSHOT"
ThisBuild / scalaVersion := scalaVersions.head

lazy val root = (project in file("."))
  .settings(nocomma {
    name := "Eval"
    libraryDependencies ++= {
      val sv = scalaVersion.value
      Seq(
        "org.scala-lang" %% "scala3-compiler" % sv,
        verify % Test,
        ioProj % Test,
      )
    }
    testFrameworks += new TestFramework("verify.runner.Framework")
    crossVersion := CrossVersion.full
    Test / fork := true
    crossScalaVersions := scalaVersions
  })

ThisBuild / semanticdbEnabled := true
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
  val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
  if (version.value.endsWith("-SNAPSHOT")) Some("central-snapshots" at centralSnapshots)
  else localStaging.value
}
