import sbt._

object Dependencies {
  val sbtV = "1.9.9"
  val scala3 = "3.4.0"

  val verify = "com.eed3si9n.verify" %% "verify" % "1.0.0"
  val scalaCompiler = "org.scala-lang" %% "scala3-compiler" % scala3
  val ioProj = "org.scala-sbt" %% "io" % sbtV
}
