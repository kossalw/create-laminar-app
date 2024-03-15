import mill._
import mill.define.Task
import mill.scalalib._
import mill.scalajslib._
import mill.scalajslib.api._

import $file.Base
import Base.AppScalaJSModule

import $file.Shared
import Shared.SharedModule

import $file.ScalablyTyped
import ScalablyTyped.`scalablytyped-module`

import $file.Dependencies
import Dependencies.Deps._

trait WebModule extends AppScalaJSModule {
  def moduleKind = ModuleKind.ESModule
  def moduleSplitStyle = ModuleSplitStyle.SmallModulesFor(List("web"))

  def moduleDeps = Seq(`scalablytyped-module`, SharedModule.js)

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