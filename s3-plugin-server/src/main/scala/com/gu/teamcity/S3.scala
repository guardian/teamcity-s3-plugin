package com.gu.teamcity

import java.io.InputStream

import com.amazonaws.auth.{AWSCredentialsProviderChain, DefaultAWSCredentialsProviderChain}
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import jetbrains.buildServer.serverSide.SBuild

import scala.util.{Success, Try}

class S3(config: S3ConfigManager) {
  val client = new AmazonS3Client({
    val provider = new AWSCredentialsProviderChain(config, new DefaultAWSCredentialsProviderChain())
    provider.setReuseLastProvider(false)
    provider
  })

  def upload(targetBucket: Option[String], build: SBuild, fileName: String, contents: InputStream): Try[Boolean] =
    (for (bucket <- targetBucket) yield
      Try {
        val uploadDirectory = s"${S3Plugin.cleanFullName(build)}/${build.getBuildNumber}"
        client.putObject(bucket, s"$uploadDirectory/$fileName", contents, new ObjectMetadata)
        true
      }
    ) getOrElse (Success(false))
}
