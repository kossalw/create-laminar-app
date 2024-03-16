import mill._
import mill.util.Jvm
import mill.define.Task
import mill.scalalib._
import mill.scalajslib._
import mill.scalajslib.api._

import $file.Base
import Base.AppScalaJSModule

import $file.Shared
import Shared.SharedModule

import $file.Dependencies
import Dependencies.Deps._

trait WebModule extends AppScalaJSModule {
  def moduleKind = ModuleKind.ESModule
  def moduleSplitStyle = ModuleSplitStyle.SmallModulesFor(List("web"))

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

  val webDeps = dom ++ laminar

  override def ivyDeps = webDeps

  object test extends ScalaJSTests with TestModule.Utest {
    // Test dependencies
    def ivyDeps = super.ivyDeps() ++ webDeps ++ utest
    def moduleKind = ModuleKind.ESModule
    def jsEnvConfig = JsEnvConfig.JsDom()
    def moduleSplitStyle = ModuleSplitStyle.FewestModules

    // JsDom cannot run fastLinkJSTest because it contains modules,
    // so we transform the result with vite.js
    override def fastLinkJSTest = T {
      val dest = T.dest
      val report = super.fastLinkJSTest()
      Jvm.runSubprocess(
        commandArgs = Seq(
          "npm",
          "run",
          "build",
          "--",
          "--mode",
          s"test:${report.dest.path}",
          "--outDir",
          dest.toString
        ),
        envArgs = Map(),
        workingDir = T.workspace
      )
      val jsFolder = dest / "assets"
      Report(
        publicModules = os
          .list(jsFolder)
          .map(f =>
            Report.Module(
              moduleID = "main",
              jsFileName = f.last,
              sourceMapName = None,
              moduleKind = ModuleKind.NoModule
            )
          ),
        dest = PathRef(jsFolder)
      )
    }
  }
}
