import mill._
import mill.scalalib._

import $file.Base
import Base.{AppScalaJSModule, AppScalaModule}

import $file.ScalablyTyped
import ScalablyTyped.`scalablytyped-module`

trait SharedModule extends Module {
  trait Shared extends AppScalaModule with PlatformScalaModule

  object jvm extends Shared
  object js extends Shared with AppScalaJSModule {
    def moduleDeps = Seq(`scalablytyped-module`)
  }
}
