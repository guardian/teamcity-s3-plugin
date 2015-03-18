package com.gu.teamcity

import jetbrains.buildServer.controllers.admin.AdminPage
import jetbrains.buildServer.serverSide.auth.Permission
import jetbrains.buildServer.web.openapi.{Groupable, PagePlaces, PluginDescriptor}
import javax.servlet.http.HttpServletRequest
import java.util.Map

class S3ConfigPage(extension: S3Extension, pagePlaces: PagePlaces, descriptor: PluginDescriptor)
  extends AdminPage(pagePlaces, "S3", descriptor.getPluginResourcesPath("input.jsp"), "S3") {

  register()

  override def fillModel(model: Map[String, AnyRef], request: HttpServletRequest) {
    model.put("bucketName", extension.bucketName.getOrElse(""))
  }

  override def isAvailable(request: HttpServletRequest): Boolean = {
    super.isAvailable(request) && checkHasGlobalPermission(request, Permission.CHANGE_SERVER_SETTINGS)
  }

  def getGroup: String = {
    Groupable.SERVER_RELATED_GROUP
  }
}