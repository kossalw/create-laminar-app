import mill._
import scalalib._

import $file.Base
import Base.AppScalaModule

import $file.Dependencies
import Dependencies.Deps._

trait GraphqlModule extends AppScalaModule {
  override def ivyDeps = osDep ++ zio ++ zioJson ++ caliban ++ calibanTools

  def createGQLSchema: T[PathRef] = T {
    val dest = T.ctx.dest / "schema.gql"
    mill.modules.Jvm.runSubprocess(
      "gql.GenGQLSchema",
      runClasspath().map(_.path),
      forkArgs(),
      forkEnv(),
      Seq(dest.toString),
      workingDir = forkWorkingDir(),
      useCpPassingJar = runUseArgsFile()
    )
    PathRef(dest)
  }

  def createGQLClient: T[PathRef] = T {
    val dest = T.ctx().dest / "client.scala"
    mill.modules.Jvm.runSubprocess(
      "gql.GenGQLClient",
      runClasspath().map(_.path),
      forkArgs(),
      forkEnv(),
      Seq(createGQLSchema().path.toString, dest.toString, "gql", "Client"),
      workingDir = forkWorkingDir(),
      useCpPassingJar = runUseArgsFile()
    )
    PathRef(dest)
  }
}
