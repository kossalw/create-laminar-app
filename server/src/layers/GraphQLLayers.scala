package server.layers

import zio.*
import http.*

import caliban.{CalibanError, GraphQLInterpreter, ZHttpAdapter}
import CalibanError.ValidationError
import caliban.interop.tapir.{HttpInterpreter, WebSocketInterpreter}
import sttp.tapir.json.zio.*

import gql.GraphQL

case class GraphQLRequestHandlers(
  httpService: RequestHandler[Any, Nothing],
  websocketService: RequestHandler[Any, Nothing]
)

object GraphQLLayers {
  val gqlServices: ZLayer[Any, ValidationError, GraphQLRequestHandlers] =
    ZLayer.fromZIO {
      GraphQL.api.interpreter
        .map { interpreter =>
          GraphQLRequestHandlers(
            httpService = ZHttpAdapter.makeHttpService(HttpInterpreter(interpreter)),
            websocketService = ZHttpAdapter.makeWebSocketService(WebSocketInterpreter(interpreter))
          )
        }
    }
}
