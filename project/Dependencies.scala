import sbt._

object Dependencies {
  val sbtV = "1.6.0"
  val scala3 = "3.2.0"

  val verify = "com.eed3si9n.verify" %% "verify" % "1.0.0"
  val scalaCompiler = "org.scala-lang" %% "scala3-compiler" % scala3
  val ioProj = "org.scala-sbt" %% "io" % sbtV
}
