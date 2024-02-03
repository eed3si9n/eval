Eval
====

Eval evaluates Scala 3 code.

Example Usage
=============

In your `build.sbt` type this or similar:
```scala
ThisBuild / scalaVersion := "3.3.1"
libraryDependencies += ("com.eed3si9n.eval" % "eval" % "0.3.0").cross(CrossVersion.full)
Compile / fork := true
```


In your `Main.scala` type this or similar:
```scala
package example

import com.eed3si9n.eval.Eval

case class ServerConfig(port: Int)

@main def main(): Unit =
  val x = Eval[ServerConfig](
    "example.ServerConfig(port = 8080)")
  println(x.port)
```

Run in `sbt` like so:
```
sbt:eval> run
[info] compiling 1 Scala source to target/scala-3.3.1/classes ...
[info] running (fork) example.main
[info] 8080
```

This shows that the string example.ServerConfig(port = 8080) was evaluated as a Scala code, 
and became available to the main function as an instantiated case class.

More examples here: https://eed3si9n.com/eval/ 