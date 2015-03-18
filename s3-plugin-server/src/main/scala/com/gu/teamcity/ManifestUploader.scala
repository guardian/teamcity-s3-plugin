package com.gu.teamcity

import java.io.ByteArrayInputStream
import java.util.Date

import jetbrains.buildServer.messages.{BuildMessage1, DefaultMessagesInfo, Status}
import jetbrains.buildServer.serverSide.{BuildServerAdapter, BuildServerListener, SRunningBuild}
import jetbrains.buildServer.util.EventDispatcher
import org.joda.time.{DateTimeZone, DateTime}
import org.json4s.JsonAST.JObject
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

class ManifestUploader(eventDispatcher: EventDispatcher[BuildServerListener], s3: S3) extends BuildServerAdapter {

  eventDispatcher.addListener(this)

  override def beforeBuildFinish(runningBuild: SRunningBuild) {
    import scala.collection.convert.wrapAsScala._

    val properties = Seq(
      "ProjectName" -> runningBuild.getFullName.replaceAll("""\s+""",  ""),
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

    val propertiesJSON = pretty(render(properties.foldLeft(JObject())(_ ~ _)))

    s3.upload(runningBuild, "build.json", new ByteArrayInputStream(propertiesJSON.getBytes("UTF-8")))

    runningBuild.addBuildMessage(normalMessage("Manifest S3 upload complete"))
  }

  private def normalMessage(text: String) =
    new BuildMessage1(DefaultMessagesInfo.SOURCE_ID, DefaultMessagesInfo.MSG_TEXT, Status.NORMAL, new Date, text)
}
