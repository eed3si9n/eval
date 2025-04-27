package testpkg

import verify.*
import com.eed3si9n.eval.Eval

object GetValueTest extends BasicTestSuite:
  test("getValue can be applied if it's a function") {
    val code =
      """
        | def test(): Unit =
        |   println("Hello, world!")
        |   scala.util.Try(1).recover {case ex: Throwable => 2}.get
        |
        | test _
        |""".stripMargin
    val i = 0
    val eval = Eval()
    val result = eval
      .evalInfer(code)
      .getValue(this.getClass.getClassLoader).asInstanceOf[() => Unit]
    val x = result.apply()
    assert(x == (()))
  }
end GetValueTest
