package com.gu.teamcity

import jetbrains.buildServer.serverSide.MainConfigProcessor
import jetbrains.buildServer.serverSide.SBuildServer
import org.jdom.Element

class S3Extension(server: SBuildServer) extends MainConfigProcessor {
  var bucketName: Option[String] = None
  server.registerExtension(classOf[MainConfigProcessor], "S3", this)

  def readFrom(root: Element) {
    for {
      s3 <- Option(root.getChild(S3Extension.s3Element))
      bucket <- Option(s3.getChild(S3Extension.bucketElement))
    }
      bucketName = Option(root.getAttributeValue("name"))
  }

  def writeTo(root: Element) {
    for (name <- bucketName) {
      val s3 = new Element(S3Extension.s3Element)
      val bucket = new Element(S3Extension.bucketElement)
      bucket.setAttribute(S3Extension.bucketElement, name)
      s3.addContent(bucket)
      root.addContent(s3)
    }
  }
}

object S3Extension {
  val bucketElement = "bucket"
  val s3Element = "S3"
}