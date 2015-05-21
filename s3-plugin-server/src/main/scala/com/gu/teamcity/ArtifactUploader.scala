package com.gu.teamcity

import java.util.Date

import com.amazonaws.ResetException
import jetbrains.buildServer.messages.{BuildMessage1, DefaultMessagesInfo, Status}
import jetbrains.buildServer.serverSide.artifacts.BuildArtifacts.BuildArtifactsProcessor
import jetbrains.buildServer.serverSide.artifacts.BuildArtifacts.BuildArtifactsProcessor.Continuation
import jetbrains.buildServer.serverSide.artifacts.{BuildArtifact, BuildArtifactsViewMode}
import jetbrains.buildServer.serverSide.{BuildServerAdapter, SRunningBuild}

import scala.util.{Try, Failure}
import scala.util.control.NonFatal

class ArtifactUploader(config: S3ConfigManager, s3: S3) extends BuildServerAdapter {

  override def beforeBuildFinish(runningBuild: SRunningBuild) {
    def report(msg: BuildMessage1): Unit = {
      runningBuild.addBuildMessage(msg)
    }

    def upload(artifact: BuildArtifact): Try[Continuation] = {
      s3.upload(config.artifactBucket, runningBuild, artifact.getName, artifact.getInputStream, artifact.getSize)
        .map { uploaded =>
          if (uploaded) {
            Continuation.CONTINUE
          } else {
            report(info("Not configured for uploading"))
            Continuation.BREAK
          }
      }
    }

    report(info("About to upload artifacts to S3"))

    if (runningBuild.isArtifactsExists) {
      runningBuild.getArtifacts(BuildArtifactsViewMode.VIEW_DEFAULT).iterateArtifacts(new BuildArtifactsProcessor {

        def processBuildArtifact(buildArtifact: BuildArtifact) = {
          if (buildArtifact.isFile || buildArtifact.isArchive)
            upload(buildArtifact)
              .recoverWith { case e: ResetException =>
                report(warn(s"Retrying artifact upload after error: ${e.getMessage}"))
                upload(buildArtifact)
              }
              .recoverWith { case NonFatal(e) =>
                report(fail(s"Error uploading artifacts: $e"))
                Failure(e)
              }.get
          else
            Continuation.CONTINUE
        }

      })
    }

    report(info("Artifact S3 upload complete"))
  }

  private def info(text: String) =
    new BuildMessage1(DefaultMessagesInfo.SOURCE_ID, DefaultMessagesInfo.MSG_TEXT, Status.NORMAL, new Date, text)
  private def warn(text: String) =
    new BuildMessage1(DefaultMessagesInfo.SOURCE_ID, DefaultMessagesInfo.MSG_BUILD_PROBLEM, Status.WARNING, new Date, text)
  private def fail(text: String) =
    new BuildMessage1(DefaultMessagesInfo.SOURCE_ID, DefaultMessagesInfo.MSG_BUILD_FAILURE, Status.FAILURE, new Date, text)
}
