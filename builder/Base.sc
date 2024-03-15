import mill._, mill.scalalib._, mill.scalajslib._

trait AppScalaModule extends ScalaModule {
  def scalaVersion = "3.3.1"
}

trait AppScalaJSModule extends AppScalaModule with ScalaJSModule {
  def scalaJSVersion = "1.15.0"
}