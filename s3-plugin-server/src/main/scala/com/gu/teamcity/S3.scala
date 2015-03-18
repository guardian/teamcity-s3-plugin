package com.gu.teamcity

import java.io.InputStream
import java.util.Date

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import jetbrains.buildServer.messages.{BuildMessage1, DefaultMessagesInfo, Status}
import jetbrains.buildServer.serverSide.SRunningBuild

import scala.util.Try
import scala.util.control.NonFatal

class S3(extension: S3ConfigManager) {
  val client = new AmazonS3Client()

  def upload(build: SRunningBuild, fileName: String, contents: InputStream): Boolean =
    (for (bucket <- extension.bucketName) yield
      Try {
        val uploadDirectory = s"${build.getProjectExternalId}/${build.getBuildTypeName}/${build.getBuildNumber}"
        client.putObject(bucket, s"$uploadDirectory/$fileName", contents, new ObjectMetadata)
        true
      } recover {
        case NonFatal(e) => {
          build.addBuildMessage(new BuildMessage1(DefaultMessagesInfo.SOURCE_ID, DefaultMessagesInfo.MSG_BUILD_FAILURE, Status.ERROR, new Date, s"Error uploading artifacts: ${e.getMessage}"))
          false
        }
      } get
    ) getOrElse (false)
}
