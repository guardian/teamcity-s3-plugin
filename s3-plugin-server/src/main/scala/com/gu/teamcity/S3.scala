package com.gu.teamcity

import java.io.InputStream

import com.amazonaws.auth.{AWSCredentialsProviderChain, DefaultAWSCredentialsProviderChain}
import com.amazonaws.event.{ProgressEvent, ProgressListener}
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{PutObjectRequest, ObjectMetadata}
import com.amazonaws.services.s3.transfer.{PersistableTransfer, TransferManager}
import com.amazonaws.services.s3.transfer.internal.{S3ProgressListenerChain, TransferManagerUtils, S3ProgressListener}
import jetbrains.buildServer.serverSide.SBuild

import scala.concurrent.Promise
import scala.util.{Success, Try}

class S3(config: S3ConfigManager) {
  val credentialsProvider = {
    val provider = new AWSCredentialsProviderChain(config, new DefaultAWSCredentialsProviderChain())
    provider.setReuseLastProvider(false)
    provider
  }

  val transferManager = new TransferManager(credentialsProvider)

  def upload(targetBucket: Option[String], build: SBuild, fileName: String, contents: InputStream, fileSize: Long): Try[Boolean] =
    (for (bucket <- targetBucket) yield
      Try {
        val uploadDirectory = s"${S3Plugin.cleanFullName(build)}/${build.getBuildNumber}"
        val metadata = {
          val md = new ObjectMetadata()
          md.setContentLength(fileSize)
          md
        }
        val req = new PutObjectRequest(bucket, s"$uploadDirectory/$fileName", contents, metadata)
        val upload = transferManager.upload(req)
        upload.waitForUploadResult()
        true
      }
    ) getOrElse (Success(false))
}
