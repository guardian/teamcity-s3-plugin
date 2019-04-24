package com.gu.teamcity

import jetbrains.buildServer.serverSide.{SBuild, BuildServerListener}
import jetbrains.buildServer.util.EventDispatcher

class S3Plugin(eventDispatcher: EventDispatcher[BuildServerListener], s3: S3, s3ConfigManager: S3ConfigManager) {
  eventDispatcher.addListener(new ArtifactUploader(s3ConfigManager, s3))
  eventDispatcher.addListener(new ManifestUploader(s3ConfigManager, s3))
  eventDispatcher.addListener(new TagUploader(s3ConfigManager, s3))
}

object S3Plugin {
  def cleanFullName(build: SBuild): String = build.getFullName.split(" / ").mkString("::")
}