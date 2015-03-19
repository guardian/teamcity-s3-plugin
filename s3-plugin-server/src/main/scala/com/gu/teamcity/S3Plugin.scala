package com.gu.teamcity

import jetbrains.buildServer.serverSide.BuildServerListener
import jetbrains.buildServer.util.EventDispatcher

class S3Plugin(eventDispatcher: EventDispatcher[BuildServerListener], s3: S3) {
  eventDispatcher.addListener(new ArtifactUploader(s3))
  eventDispatcher.addListener(new ManifestUploader(s3))
  eventDispatcher.addListener(new TagUploader(s3))
}
