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

class ManifestUploader(s3: S3) extends BuildServerAdapter {

  override def beforeBuildFinish(runningBuild: SRunningBuild) {
    import scala.collection.convert.wrapAsScala._

    if (!runningBuild.isHasInternalArtifactsOnly) {
      val properties = Seq(
        "ProjectName" -> S3Plugin.cleanFullName(runningBuild),
        "BuildNumber" -> runningBuild.getBuildNumber,
        "StartTime" -> new DateTime(runningBuild.getStartDate).withZone(DateTimeZone.UTC).toString //Joda default is ISO8601
      ) ++ runningBuild.getRevisions.flatMap(revision => Seq(
        "Revision" -> revision.getRevision,
        "VCS" -> revision.getRoot.getProperties.get("url")
      )) ++ Option(runningBuild.getBranch).map(b =>
        "Branch" -> b.getDisplayName
      ).orElse(runningBuild.getVcsRootEntries.headOption.map(r =>
        "Branch" -> r.getProperties.get("branch")
      ))

      val propertiesJSON = pretty(render(properties.foldLeft(JObject())(_ ~ _)))

      s3.upload(runningBuild, "build.json", new ByteArrayInputStream(propertiesJSON.getBytes("UTF-8"))) match {
        case Failure(e) => runningBuild.addBuildMessage(new BuildMessage1(DefaultMessagesInfo.SOURCE_ID, DefaultMessagesInfo.MSG_BUILD_FAILURE, Status.ERROR, new Date,
          s"Error uploading manifest: ${e.getMessage}"))
        case Success(status) => if (status) runningBuild.addBuildMessage(normalMessage("Manifest S3 upload complete"))
      }
    }
  }

  private def normalMessage(text: String) =
    new BuildMessage1(DefaultMessagesInfo.SOURCE_ID, DefaultMessagesInfo.MSG_TEXT, Status.NORMAL, new Date, text)
}
