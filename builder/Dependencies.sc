import mill._, mill.scalalib._

object Deps {
  // Scalajs dependencies
  val dom = Agg(ivy"org.scala-js::scalajs-dom::2.8.0")

  val laminarVersion = "16.0.0"
  val laminar = Agg(
    ivy"com.raquo::laminar::$laminarVersion",
    ivy"com.raquo::airstream::$laminarVersion"
  )

  // Scala dependencies
  val osDep = Agg(ivy"com.lihaoyi::os-lib:0.9.3")

  val zioVersion = "2.1-RC1"
  val zio = Agg(
    ivy"dev.zio::zio:$zioVersion",
    ivy"dev.zio::zio-streams:$zioVersion",
    ivy"dev.zio::zio-logging:2.2.2"
  )
  val zioHttp = Agg(ivy"dev.zio::zio-http:3.0.0-RC4")
  val zioJson = Agg(ivy"dev.zio::zio-json:0.6.2")
  val tapirZioJson = Agg(ivy"com.softwaremill.sttp.tapir::tapir-json-zio:1.10.0")

  val calibanVersion = "2.5.3"
  val caliban = Agg(ivy"com.github.ghostdogpr::caliban:$calibanVersion")
  val calibanZio = Agg(ivy"com.github.ghostdogpr::caliban-zio-http:$calibanVersion")
  val calibanTools = Agg(ivy"com.github.ghostdogpr::caliban-tools:$calibanVersion")

  val pprintDep = Agg(ivy"com.lihaoyi::pprint:0.7.0")

  // Test
  val utest = Agg(ivy"com.lihaoyi::utest::0.8.2")

  val zioTestDeps = Agg(
    ivy"dev.zio::zio-test:$zioVersion",
    ivy"dev.zio::zio-test-sbt:$zioVersion",
    ivy"dev.zio::zio-test-magnolia:$zioVersion"
  )
}
