package com.eed3si9n.eval

import dotty.tools.dotc.reporting.Reporter
import java.nio.file.{ Files, Path }
import sbt.io.IO
import verify.*

object EvalTest extends BasicTestSuite:
  var reporter: Reporter = null
  def mkReporter(): Reporter =
    // reporter = EvalReporter.store
    reporter = EvalReporter.console
    reporter

  def eval(backing: Path) = Eval(
    backing,
    mkReporter,
  )

  def eval(nonCpOptions: Seq[String], backing: Path) = Eval(
    nonCpOptions,
    backing,
    mkReporter,
  )

  test("inferred integer - virtual dir + no reporter") {
    val i = 0
    val eval = Eval()
    val result = eval.evalInfer(i.toString)
    assert(result.tpe == IntType)
    assert(value(result) == 0)
  }

  test("inferred integer - virtual dir") {
    val i = 1
    val eval = Eval(() => mkReporter())
    val result = eval.evalInfer(i.toString)
    assert(result.tpe == IntType)
    assert(value(result) == 1)
  }

  test("inferred integer") {
    IO.withTemporaryDirectory { tempDir =>
      val i = 2
      val result = eval(tempDir.toPath).evalInfer(i.toString)
      assert(result.tpe == IntType)
      assert(value(result) == 2)
    }
  }

  test("explicit long") {
    IO.withTemporaryDirectory { tempDir =>
      val i = 3
      val result = eval(tempDir.toPath).eval(i.toString, tpeName = Some(LongType))
      assert(result.tpe == LongType)
      assert(value(result) == 3)
    }
  }

  test("macro explicit type") {
    val i = 3
    val result = Eval[ServerConfig]("""
      |com.eed3si9n.eval.ServerConfig(
      |  port = 8081,
      |)""".stripMargin)
    assert(result.port == 8081)
  }

  test("type mismatch") {
    IO.withTemporaryDirectory { tempDir =>
      val i = 4
      intercept[EvalException] {
        try {
          eval(tempDir.toPath).eval(i.toString, tpeName = Some(BooleanType))
        } catch {
          case e: EvalException =>
            throw e
        }
      }
      hasErrors(1, "")
    }
  }

  test("type mismatch2") {
    IO.withTemporaryDirectory { tempDir =>
      val i = 5
      intercept[EvalException] {
        try {
          eval(tempDir.toPath).eval(i.toString, tpeName = Some(StringType))
        } catch {
          case e: EvalException =>
            throw e
        }
      }
      hasErrors(1, "")
    }
  }

  test("caching integer") {
    IO.withTemporaryDirectory { tempDir =>
      val i = 2
      val result = eval(tempDir.toPath).evalInfer(i.toString)
      assert(result.tpe == IntType)
      assert(value(result) == 2)

      val result2 = eval(tempDir.toPath).evalInfer(i.toString)
      assert(result2.tpe == IntType)
      assert(value(result2) == 2)
    }
  }

  test("explicit import") {
    IO.withTemporaryDirectory { tempDir =>
      val i = -6
      val imports = EvalImports(Seq("import math.abs"))
      val result = eval(tempDir.toPath).evalInfer(s"""abs($i)""", imports)
      assert(result.tpe == IntType)
      assert(value(result) == 6)
    }
  }

  test("wildcard import") {
    IO.withTemporaryDirectory { tempDir =>
      val i = -7
      val imports = EvalImports(Seq("import math.*"))
      val result = eval(tempDir.toPath).evalInfer(s"""abs($i)""", imports)
      assert(result.tpe == IntType)
      assert(value(result) == 7)
    }
  }

  test("options") {
    IO.withTemporaryDirectory { tempDir =>
      val i = 5
      intercept[EvalException] {
        // pure expression warning
        eval(Seq("-Xfatal-warnings"), tempDir.toPath)
          .evalInfer(s"{ 1; $i }")
      }
      hasErrors(1, "")
    }
  }

  test("val test") {
    IO.withTemporaryDirectory { tempDir =>
      val defs = (valTestContent, 1 to 7) :: Nil
      val res =
        eval(tempDir.toPath).evalDefinitions(
          defs,
          new EvalImports(Nil),
          "<defs>",
          "scala.Int" :: Nil
        )
      assert(res.valNames.toSet == valTestNames)
    }
  }

  test("val position") {
    IO.withTemporaryDirectory { tempDir =>
      intercept[EvalException] {
        val defs = (badValCode, 1 to 7) :: Nil
        val res =
          eval(tempDir.toPath).evalDefinitions(
            defs,
            new EvalImports(Seq("import math.*")),
            "<defs>",
            "scala.Int" :: Nil
          )
      }
      hasErrors(5, "")
    }
  }

  test("lazy val test") {
    IO.withTemporaryDirectory { tempDir =>
      val defs = (lazyValTestContent, 1 to 3) :: Nil
      val res =
        eval(tempDir.toPath).evalDefinitions(
          defs,
          new EvalImports(Nil),
          "<defs>",
          "scala.Int" :: Nil
        )
      assert(res.valNames.toSet == Set("x"))
      assert(res.values(getClass.getClassLoader) == Seq(7))
    }
  }

  private[this] def hasErrors(line: Int, src: String) = {
    val errors = reporter.allErrors
    assert(errors.nonEmpty)
    assert(errors.head.pos.line == line - 1)
  }

  lazy val valTestNames = Set("x", "a")
  lazy val valTestContent = """
val x: Int =
  val y: Int = 4
  y

val z: Double = 3.0
val a = 9
val p = {
  object B:
    val i = 3
  end B

  class C { val j = 4 }
  "asdf"
}
"""

  lazy val badValCode = """val x: Int =
  val y: Int = 4
  y

val a: String = 9
"""

  lazy val lazyValTestContent = """
lazy val x: Int = 7
"""

  lazy val IntType = "Int"
  lazy val LongType = "Long"
  lazy val BooleanType = "Boolean"
  lazy val StringType = "String"
  private[this] def value(r: EvalResult) = r.getValue(getClass.getClassLoader)
end EvalTest
