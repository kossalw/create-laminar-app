import mill._
import mill.define.Task
import mill.scalalib._
import mill.scalajslib._
import mill.scalajslib.api._
import Deps._

// Helpers

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

trait AppScalaModule extends ScalaModule {
  def scalaVersion = "3.3.1"
}

trait AppScalaJSModule extends AppScalaModule with ScalaJSModule {
  def scalaJSVersion = "1.15.0"
}

// Typescript support
import $ivy.`com.github.lolgab::mill-scalablytyped::0.1.12`
import com.github.lolgab.mill.scalablytyped._

object `scalablytyped-module` extends AppScalaJSModule with ScalablyTyped

// Modules
object shared extends Module {
  trait SharedModule extends AppScalaModule with PlatformScalaModule {
    def ivyDeps = super.ivyDeps() ++ jsonScalaJs
  }

  object jvm extends SharedModule
  object js extends SharedModule with AppScalaJSModule {
    def moduleDeps = Seq(`scalablytyped-module`)
  }
}

object web extends AppScalaJSModule {
  def moduleKind = ModuleKind.ESModule
  def moduleSplitStyle = ModuleSplitStyle.SmallModulesFor(List("web"))

  def moduleDeps = Seq(`scalablytyped-module`, shared.js)

  def publicDev = T {
    public(fastLinkJS)()
  }

  def publicProd = T {
    public(fullLinkJS)()
  }

  // This will output a JSON while using mill show and we'll it to alias to
  // import { main } from "@public/main.js"
  def public(jsTask: Task[Report]): Task[Map[String, os.Path]] = T.task {
    Map("@public" -> jsTask().dest.path)
  }

  override def ivyDeps = super.ivyDeps() ++ dom ++ laminar ++ jsonScalaJs
}

object server extends AppScalaModule {
  def moduleDeps = Seq(shared.jvm)

  override def ivyDeps = super.ivyDeps() ++ zio ++ jsonScala ++ pprintDep
}