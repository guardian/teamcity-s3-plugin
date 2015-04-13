package com.gu.teamcity

import java.io.ByteArrayInputStream
import java.util

import jetbrains.buildServer.serverSide.{BuildServerAdapter, SBuild}
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

class TagUploader(config: S3ConfigManager, s3: S3) extends BuildServerAdapter {

  override def buildTagsChanged(build: SBuild, oldTags: util.List[String], newTags: util.List[String]): Unit = {
    import scala.collection.convert.wrapAsScala._

    val tagJSON =pretty(render(asScalaBuffer(newTags)))
    val jsBytes = tagJSON.getBytes("UTF-8")
    s3.upload(config.tagManifestBucket, build, s"tags.json", new ByteArrayInputStream(jsBytes), jsBytes.length)
  }
}
