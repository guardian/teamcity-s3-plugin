package com.gu.teamcity

import java.io.ByteArrayInputStream
import java.util.Date

import jetbrains.buildServer.messages.{BuildMessage1, DefaultMessagesInfo, Status}
import jetbrains.buildServer.serverSide.{BuildServerAdapter, BuildServerListener, SRunningBuild}
import jetbrains.buildServer.util.EventDispatcher
import org.joda.time.{DateTimeZone, DateTime}

class ManifestUploader(eventDispatcher: EventDispatcher[BuildServerListener], s3: S3) extends BuildServerAdapter {

  eventDispatcher.addListener(this)

  override def beforeBuildFinish(runningBuild: SRunningBuild) {
    import scala.collection.convert.wrapAsScala._

    val properties = Seq(
      "ProjectName" -> runningBuild.getFullName,
      "BuildNumber" -> runningBuild.getBuildNumber,
      "StartTime" ->  new DateTime(runningBuild.getStartDate).withZone(DateTimeZone.UTC).toString //Joda default is ISO8601
    ) ++ runningBuild.getRevisions.flatMap(revision => Seq(
      "Revision" -> revision.getRevision,
      "VCS" -> revision.getRoot.getProperties.get("url")
    )) ++ Option(runningBuild.getBranch).map(b =>
      "Branch" -> b.getDisplayName
    ).orElse(runningBuild.getVcsRootEntries.headOption.map(r =>
      "Branch" -> r.getProperties.get("branch")
    ))

    val propertiesString = properties.map { case (k,v) => s"$k=$v" }.mkString("\n") + "\n"

    s3.upload(runningBuild, "build.properties", new ByteArrayInputStream(propertiesString.getBytes("UTF-8")))

    runningBuild.addBuildMessage(normalMessage("Manifest S3 upload complete"))
  }

  private def normalMessage(text: String) =
    new BuildMessage1(DefaultMessagesInfo.SOURCE_ID, DefaultMessagesInfo.MSG_TEXT, Status.NORMAL, new Date, text)
}
