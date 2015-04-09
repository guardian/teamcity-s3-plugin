package com.gu.teamcity

import java.util.Date

import jetbrains.buildServer.messages.{BuildMessage1, DefaultMessagesInfo, Status}
import jetbrains.buildServer.serverSide.artifacts.BuildArtifacts.BuildArtifactsProcessor
import jetbrains.buildServer.serverSide.artifacts.BuildArtifacts.BuildArtifactsProcessor.Continuation
import jetbrains.buildServer.serverSide.artifacts.{BuildArtifact, BuildArtifactsViewMode}
import jetbrains.buildServer.serverSide.{BuildServerAdapter, SRunningBuild}

import scala.util.control.NonFatal

class ArtifactUploader(config: S3ConfigManager, s3: S3) extends BuildServerAdapter {

  override def beforeBuildFinish(runningBuild: SRunningBuild) {
    runningBuild.addBuildMessage(normalMessage("About to upload artifacts"))

    if (runningBuild.isArtifactsExists) {
      runningBuild.getArtifacts(BuildArtifactsViewMode.VIEW_DEFAULT).iterateArtifacts(new BuildArtifactsProcessor {
        def processBuildArtifact(buildArtifact: BuildArtifact) = {
          if (buildArtifact.isFile || buildArtifact.isArchive)
            s3.upload(config.artifactBucket, runningBuild, buildArtifact.getName, buildArtifact.getInputStream) map {
              uploaded =>
                if (uploaded) {
                  Continuation.CONTINUE
                } else {
                  normalMessage("Not configured for uploading")
                  Continuation.BREAK
                }
            } recover {
              case NonFatal(e) => {
                runningBuild.addBuildMessage(new BuildMessage1(DefaultMessagesInfo.SOURCE_ID, DefaultMessagesInfo.MSG_BUILD_FAILURE, Status.ERROR, new Date,
                  s"Error uploading artifacts: ${e.getMessage}"))
                Continuation.BREAK
              }
            } get
          else
            Continuation.CONTINUE
        }
      })
    }

    runningBuild.addBuildMessage(normalMessage("Artifact S3 upload complete"))
  }

  private def normalMessage(text: String) =
    new BuildMessage1(DefaultMessagesInfo.SOURCE_ID, DefaultMessagesInfo.MSG_TEXT, Status.NORMAL, new Date, text)
}
