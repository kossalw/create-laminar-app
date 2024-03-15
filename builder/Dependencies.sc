import mill._, mill.scalalib._

object Deps {
  // Scalajs dependencies
  val dom = Agg(ivy"org.scala-js::scalajs-dom::2.8.0")

  val laminarVersion = "16.0.0"
  val laminar = Agg(
    ivy"com.raquo::laminar::$laminarVersion",
    ivy"com.raquo::airstream::$laminarVersion"
  )

  val upickleVersion = "3.2.0"
  val jsonScalaJs = Agg(
    ivy"com.lihaoyi::upickle::$upickleVersion",
    ivy"com.lihaoyi::ujson::$upickleVersion"
  )

  // Scala dependencies
  val zioVersion = "2.0.21"
  val zio = Agg(
    ivy"dev.zio::zio:$zioVersion",
    ivy"dev.zio::zio-streams:$zioVersion",
    ivy"dev.zio::zio-http:3.0.0-RC4",
  )

  val jsonScala = Agg(
    ivy"com.lihaoyi::upickle:$upickleVersion",
    ivy"com.lihaoyi::ujson:$upickleVersion"
  )

  val pprintDep = Agg(ivy"com.lihaoyi::pprint:0.7.0")
}