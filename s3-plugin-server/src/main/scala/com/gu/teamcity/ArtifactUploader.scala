package com.gu.teamcity

import java.io.File
import java.util.Date

import jetbrains.buildServer.messages.{BuildMessage1, DefaultMessagesInfo, Status}
import jetbrains.buildServer.serverSide.{BuildServerAdapter, SRunningBuild}

import scala.util.control.NonFatal

class ArtifactUploader(config: S3ConfigManager, s3: S3) extends BuildServerAdapter {

  override def beforeBuildFinish(runningBuild: SRunningBuild) {
    def report(msg: String): Unit = {
      runningBuild.getBuildLog().message(normalMessage(msg))
    }

    report("About to upload artifacts to S3")


    getAllFiles(runningBuild).foreach { case (name: String, artifact: File) =>
      config.artifactBucket match {
        case None => report("Target artifactBucket was not set")
        case Some(bucket) =>
          s3.upload(bucket, runningBuild, name, artifact).recover {
            case NonFatal(e) =>
              runningBuild.getBuildLog().message(new BuildMessage1(DefaultMessagesInfo.SOURCE_ID, DefaultMessagesInfo.MSG_BUILD_FAILURE, Status.ERROR, new Date,
                s"Error uploading artifacts: ${e.getMessage}"))
          }
      }
    }

    report("Artifact S3 upload complete")
  }

  def getAllFiles(runningBuild: SRunningBuild): Seq[(String,File)] = {
    if (!runningBuild.isArtifactsExists) {
      Nil
    } else {
      ArtifactUploader.getChildren(runningBuild.getArtifactsDirectory)
    }
  }

  private def normalMessage(text: String) =
    new BuildMessage1(DefaultMessagesInfo.SOURCE_ID, DefaultMessagesInfo.MSG_TEXT, Status.NORMAL, new Date, text)
}

object ArtifactUploader {

  def getChildren(file: File, paths: Seq[String] = Nil, current: String = ""): Seq[(String, File)] = {
    file.listFiles.toSeq.flatMap {
      child =>
        if (child.isHidden) {
          Seq()
        } else {
          val newPath = current + child.getName
          if (child.isDirectory) {
            getChildren(child, paths, newPath + File.separator)
          } else {
            Seq((newPath, child))
          }
        }
    }
  }

}