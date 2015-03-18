package com.gu.teamcity

import java.io.{PrintWriter, File}

import jetbrains.buildServer.serverSide.{ServerPaths, MainConfigProcessor, SBuildServer}
import org.jdom.Element

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization
import org.json4s.native.Serialization._

case class S3Config(bucketName: Option[String])

class S3ConfigManager(paths: ServerPaths) {
  implicit val formats = Serialization.formats(NoTypeHints)

  val configFile = new File(s"${paths.getConfigDir}/s3.json")

  private var config: Option[S3Config] = {
    if (configFile.exists()) {
      parse(configFile).extractOpt[S3Config]
    } else None
  }

  def bucketName: Option[String] = {
    config.flatMap(_.bucketName)
  }


  def update(config: S3Config): Unit = {
    synchronized {
      this.config = Some(config)
      val out = new PrintWriter(configFile, "UTF-8")
      try { writePretty(config, out) }
      finally { out.close }
    }
  }
}

object S3ConfigManager {
  val bucketElement = "bucket"
  val s3Element = "S3"
}