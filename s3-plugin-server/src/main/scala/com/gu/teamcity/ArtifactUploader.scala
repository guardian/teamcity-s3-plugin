package com.gu.teamcity

import java.util.Date

import jetbrains.buildServer.messages.{BuildMessage1, DefaultMessagesInfo, Status}
import jetbrains.buildServer.serverSide.artifacts.BuildArtifacts.BuildArtifactsProcessor
import jetbrains.buildServer.serverSide.artifacts.BuildArtifacts.BuildArtifactsProcessor.Continuation
import jetbrains.buildServer.serverSide.artifacts.{BuildArtifact, BuildArtifactsViewMode}
import jetbrains.buildServer.serverSide.{BuildServerAdapter, BuildServerListener, SRunningBuild}
import jetbrains.buildServer.util.EventDispatcher

class ArtifactUploader(eventDispatcher: EventDispatcher[BuildServerListener], s3: S3) extends BuildServerAdapter {

  eventDispatcher.addListener(this)

  override def beforeBuildFinish(runningBuild: SRunningBuild) {
    runningBuild.addBuildMessage(normalMessage("About to upload artifacts"))

    if (runningBuild.isArtifactsExists) {
      runningBuild.getArtifacts(BuildArtifactsViewMode.VIEW_DEFAULT).iterateArtifacts(new BuildArtifactsProcessor {
        def processBuildArtifact(buildArtifact: BuildArtifact) = {
          if (buildArtifact.isFile || buildArtifact.isArchive)
            if (s3.upload(runningBuild, s"artifacts/${buildArtifact.getName}", buildArtifact.getInputStream))
              Continuation.CONTINUE
            else
              Continuation.BREAK
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
