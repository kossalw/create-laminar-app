package server

import zio.*
import http.*
import logging.consoleLogger

import server.routes.{GraphQLRoutes, HealthRoutes}
import server.layers.{GraphQLLayers, GraphQLRequestHandlers}

object MainServer extends ZIOAppDefault {

  def app(gqlHandlers: GraphQLRequestHandlers) =
    (HealthRoutes.routes ++ GraphQLRoutes.routes(gqlHandlers)).toHttpApp @@ Middleware.debug

  override val bootstrap =
    Runtime.removeDefaultLoggers >>> consoleLogger()

  def run = (for {
    _ <- ZIO.logInfo("Started server at http://localhost:8080/")
    gqlHandlers <- ZIO.service[GraphQLRequestHandlers]
    _ <- Server.serve(app(gqlHandlers))
  } yield ()).provide(Server.default ++ GraphQLLayers.gqlServices).exitCode
}
