import mill._
import mill.define.Task
import mill.scalalib._

import $ivy.`com.lihaoyi::mill-contrib-docker:$MILL_VERSION`
import contrib.docker.DockerModule

import $file.Base
import Base.AppScalaModule

import $file.Shared
import Shared.SharedModule

import $file.Dependencies
import Dependencies.Deps._

trait ServerModule extends AppScalaModule with DockerModule {
  override def moduleDeps = Seq(SharedModule.jvm)

  override def mainClass: T[Option[String]] = Some("server.MainServer")

  override def ivyDeps = super.ivyDeps() ++ zio ++ jsonScala ++ pprintDep

  object docker extends DockerConfig {
    def gitCommit: Task[String] = T {
      os.proc("git", "rev-parse", "--short", "HEAD").call().out.text().trim()
    }

    override def tags = List(s"repository/server:${gitCommit()}")

    override def baseImage = "gcr.io/distroless/java21:latest"

    override def labels = Map("version" -> gitCommit())

    // UDP ports the container will listen to
    override def exposedUdpPorts = Seq.empty[Int]

    // JVM runtime options such as heap size settings
    def jvmOptions: T[Seq[String]] = Seq(
      "-Xms256M",
      "-Xmx1G",
      "-XX:+UnlockExperimentalVMOptions",
      "-XX:+UseStringDeduplication",
      "-XX:+UseG1GC",
      "-XX:G1NewSizePercent=20",
      "-XX:G1ReservePercent=20",
      "-XX:MaxGCPauseMillis=50",
      "-XX:G1HeapRegionSize=32M",
      "-XX:+HeapDumpOnOutOfMemoryError"
    )

    def mainClassArg: T[Seq[String]] = Seq.empty[String]

    def jarName: T[String] = assembly().path.last

    def jarArg: T[Seq[String]] = T { 
      mainClass() match {
        case Some(main) => Seq("-cp", s"/${jarName()}", main)
        case None => Seq("-jar", s"/$jarName")
      }
    }

    def entrypoint: T[Seq[String]] = Seq("java") ++ jvmOptions() ++ jarArg() ++ mainClassArg()

    // https://github.com/com-lihaoyi/mill/blob/a3d3dc3e14620cd40695b237adb7655ef042c49e/contrib/docker/src/mill/contrib/docker/DockerModule.scala#L97
    override def dockerfile: T[String] = T {
      val jarName = assembly().path.last
      val labelRhs = labels()
        .map { case (k, v) =>
          val lineBrokenValue = v
            .replace("\r\n", "\\\r\n")
            .replace("\n", "\\\n")
            .replace("\r", "\\\r")
          s""""$k"="$lineBrokenValue""""
        }
        .mkString(" ")

      val lines = List(
        if (labels().isEmpty) "" else s"LABEL $labelRhs",
        if (exposedPorts().isEmpty) ""
        else exposedPorts().map(port => s"$port/tcp")
          .mkString("EXPOSE ", " ", ""),
        if (exposedUdpPorts().isEmpty) ""
        else exposedUdpPorts().map(port => s"$port/udp")
          .mkString("EXPOSE ", " ", ""),
        envVars().map { case (env, value) =>
          s"ENV $env=$value"
        }.mkString("\n"),
        if (volumes().isEmpty) ""
        else volumes().map(v => s"\"$v\"").mkString("VOLUME [", ", ", "]"),
        run().map(c => s"RUN $c").mkString("\n"),
        if (user().isEmpty) "" else s"USER ${user()}"
      ).filter(_.nonEmpty).mkString(sys.props("line.separator"))

      val quotedEntryPointArgs = entrypoint().map(arg => s"\"$arg\"").mkString(", ")

      s"""
         |FROM ${baseImage()}
         |$lines
         |COPY $jarName /$jarName
         |ENTRYPOINT [$quotedEntryPointArgs]""".stripMargin
    }
  }
}