import mill._

import $file.builder.{Graphql, ScalablyTyped, Server, Shared, Web}
import ScalablyTyped.`scalablytyped-module`

object shared extends Shared.SharedModule

object graphql extends Graphql.GraphqlModule

object web extends Web.WebModule {
  def moduleDeps = Seq(`scalablytyped-module`, shared.js, graphql)

  def generatedSources = Seq(graphql.createGQLClient())
}

object server extends Server.ServerModule {
  def moduleDeps = Seq(shared.jvm, graphql)

  def generatedSources = Seq(graphql.createGQLClient())
}
