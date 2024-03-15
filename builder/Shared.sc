import mill._
import mill.scalalib._

import $file.Base
import Base.{AppScalaModule, AppScalaJSModule}

import $file.ScalablyTyped
import ScalablyTyped.`scalablytyped-module`

import $file.Dependencies
import Dependencies.Deps.jsonScalaJs

object SharedModule extends Module {
  trait Shared extends AppScalaModule with PlatformScalaModule {
    def ivyDeps = super.ivyDeps() ++ jsonScalaJs
  }

  object jvm extends Shared
  object js extends Shared with AppScalaJSModule {
    def moduleDeps = Seq(`scalablytyped-module`)
  }
}