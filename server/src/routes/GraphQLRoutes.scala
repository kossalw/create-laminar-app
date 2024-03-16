package server.routes

import zio.*
import http.*

import server.layers.GraphQLRequestHandlers

object GraphQLRoutes {
  def routes(handlers: GraphQLRequestHandlers) =
    Routes(
      Method.POST / "api" / "graphql" -> handlers.httpService,
      Method.GET / "ws" / "graphql" -> handlers.websocketService
    )
}
