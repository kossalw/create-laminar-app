import mill._

import $file.builder.{Shared, Web, Server}

val shared = Shared.SharedModule
object web extends Web.WebModule
object server extends Server.ServerModule