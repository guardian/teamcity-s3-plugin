package com.gu.teamcity

import java.io.ByteArrayInputStream
import java.util.Date

import jetbrains.buildServer.messages.{BuildMessage1, DefaultMessagesInfo, Status}
import jetbrains.buildServer.serverSide.{BuildServerAdapter, SRunningBuild}
import org.joda.time.{DateTime, DateTimeZone}
import org.json4s.JsonAST.JObject
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

import scala.util.{Failure, Success}

class ManifestUploader(config: S3ConfigManager, s3: S3) extends BuildServerAdapter {

  override def beforeBuildFinish(runningBuild: SRunningBuild) {
    import scala.collection.convert.wrapAsScala._

    if (!runningBuild.isHasInternalArtifactsOnly) {
      val properties = Seq(
        "projectName" -> S3Plugin.cleanFullName(runningBuild),
        "buildNumber" -> runningBuild.getBuildNumber,
        "startTime" -> new DateTime(runningBuild.getStartDate).withZone(DateTimeZone.UTC).toString //Joda default is ISO8601
      ) ++ runningBuild.getRevisions.flatMap(revision => Seq(
        "revision" -> revision.getRevision,
        "vcsURL" -> revision.getRoot.getProperties.get("url")
      )) ++ Option(runningBuild.getBranch).map(b =>
        "branch" -> b.getDisplayName
      ).orElse(runningBuild.getVcsRootEntries.headOption.map(r =>
        "branch" -> r.getProperties.get("branch")
      ))

      val propertiesJSON = pretty(render(properties.foldLeft(JObject())(_ ~ _)))
      val jsBytes = propertiesJSON.getBytes("UTF-8")

      config.buildManifestBucket.map { bucket =>
        s3.upload(bucket, runningBuild, "build.json", new ByteArrayInputStream(jsBytes), jsBytes.length) match {
          case Failure(e) => runningBuild.getBuildLog().message(new BuildMessage1(DefaultMessagesInfo.SOURCE_ID, DefaultMessagesInfo.MSG_BUILD_FAILURE, Status.ERROR, new Date,
            s"Error uploading manifest: ${e.getMessage}"))
          case Success(_) => runningBuild.getBuildLog().message(normalMessage("Manifest S3 upload complete"))
        }
      }
    }
  }

  private def normalMessage(text: String) =
    new BuildMessage1(DefaultMessagesInfo.SOURCE_ID, DefaultMessagesInfo.MSG_TEXT, Status.NORMAL, new Date, text)
}
