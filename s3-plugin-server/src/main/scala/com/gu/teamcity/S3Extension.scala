package com.gu.teamcity

import jetbrains.buildServer.serverSide.MainConfigProcessor
import jetbrains.buildServer.serverSide.SBuildServer
import org.jdom.Element

object S3Extension {
  val bucketElement: String = "bucketElement"
  val s3Element: String = "S3"
}

class S3Extension(server: SBuildServer) extends MainConfigProcessor {
  var bucketName: String = null
  server.registerExtension(classOf[MainConfigProcessor], "S3", this)

  def readFrom(root: Element) {
    val s3 = root.getChild(S3Extension.s3Element)
    if (s3 != null && s3.getChild(S3Extension.bucketElement) != null) {
      bucketName = root.getAttributeValue("name")
    }
  }

  def writeTo(root: Element) {
    if (bucketName != null) {
      val s3 = new Element(S3Extension.s3Element)
      val bucket = new Element(S3Extension.bucketElement)
      bucket.setAttribute(S3Extension.bucketElement, bucketName)
      s3.addContent(bucket)
      root.addContent(s3)
    }
  }
}