package com.gu.teamcity

import jetbrains.buildServer.controllers.admin.AdminPage
import jetbrains.buildServer.serverSide.auth.Permission
import jetbrains.buildServer.web.openapi.{Groupable, PagePlaces, PluginDescriptor}
import org.jetbrains.annotations.NotNull
import javax.servlet.http.HttpServletRequest
import java.util.Map

class S3ConfigPage(@NotNull extension: S3Extension, @NotNull pagePlaces: PagePlaces, @NotNull descriptor: PluginDescriptor)
  extends AdminPage(pagePlaces, "S3", descriptor.getPluginResourcesPath("input.jsp"), "S3") {

  register()

  override def fillModel(@NotNull model: Map[String, AnyRef], @NotNull request: HttpServletRequest) {
    model.put("bucketName", extension.bucketName)
  }

  override def isAvailable(@NotNull request: HttpServletRequest): Boolean = {
    super.isAvailable(request) && checkHasGlobalPermission(request, Permission.CHANGE_SERVER_SETTINGS)
  }

  @NotNull def getGroup: String = {
    Groupable.SERVER_RELATED_GROUP
  }
}