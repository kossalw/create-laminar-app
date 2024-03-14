package server

import zio.*
import http.*

import upickle.default.{Writer, write}

object MainServer extends ZIOAppDefault {

  private val apiRoutes =
    Routes(
      Method.GET / "api" / "health" -> handler(Response.ok)
    )

  def app = apiRoutes.toHttpApp @@ Middleware.debug

  def run =
    Console.printLine("Started server at http://localhost:8080/") <*
      Server.serve(app).provide(Server.default)
}