package com.gu.teamcity

import java.io.{File, FileOutputStream, InputStream}

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.{AWSCredentialsProviderChain, DefaultAWSCredentialsProviderChain}
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.transfer.TransferManager
import jetbrains.buildServer.serverSide.SBuild

import scala.util.{Success, Try}

class S3(config: S3ConfigManager) {
  val credentialsProvider = {
    val provider = new AWSCredentialsProviderChain(config, new DefaultAWSCredentialsProviderChain())
    provider.setReuseLastProvider(false)
    provider
  }

  val transferManager = new TransferManager(
    new AmazonS3Client(credentialsProvider, new ClientConfiguration().withMaxErrorRetry(2))
  )

  def upload(targetBucket: Option[String], build: SBuild, fileName: String, contents: InputStream): Try[Boolean] =
    (for (bucket <- targetBucket) yield
      Try {
        val uploadDirectory = s"${S3Plugin.cleanFullName(build)}/${build.getBuildNumber}"
        val req = putRequestAsFile(bucket, s"$uploadDirectory/$fileName", contents)
        val upload = transferManager.upload(req)
        upload.waitForUploadResult()
        true
      }
    ) getOrElse Success(false)

  def putRequestAsFile(bucket: String, targetName: String, contents: InputStream): PutObjectRequest = {
    // convert to a file, then the S3 client can do parallel upload, and also not fail with reset stream problems
    val file = MakeFile(contents)
    val req = new PutObjectRequest(bucket, targetName, file)
    contents.close()
    req
  }

}

/**
 * Return the input stream as a File, consuming everything but not closing it.
 */
object MakeFile {

  def apply(input: InputStream): File = {
    val file = File.createTempFile("s3-plugin", "")
    val output = new FileOutputStream(file)
    try {
      val bytes = new Array[Byte](1024)
      Iterator
        .continually(input.read(bytes))
        .takeWhile(-1 != _)
        .foreach(read => output.write(bytes, 0, read))
    } finally {
      output.close()
    }
    file
  }

}