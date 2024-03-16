package server.routes

import zio.*
import http.*

object HealthRoutes {
  val routes = Routes(
    Method.GET / "api" / "health" -> handler(Response.ok)
  )
}
