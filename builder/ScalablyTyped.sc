import mill._
import $file.Base
import Base.AppScalaJSModule

import $ivy.`com.github.lolgab::mill-scalablytyped::0.1.12`
import com.github.lolgab.mill.scalablytyped.{ScalablyTyped => TypeScriptModule, _}

object `scalablytyped-module` extends AppScalaJSModule with TypeScriptModule
