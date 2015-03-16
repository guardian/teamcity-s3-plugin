package com.gu.teamcity

import jetbrains.buildServer.controllers.MultipartFormController
import jetbrains.buildServer.web.openapi.WebControllerManager
import org.jetbrains.annotations.NotNull
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class S3ConfigController(extension: S3Extension, webControllerManager: WebControllerManager) extends MultipartFormController {
  webControllerManager.registerController("/app/s3/**", this)

  protected def doPost(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse): ModelAndView = {
    val bucket: String = httpServletRequest.getParameter("bucketName")
    extension.bucketName = if (bucket.isEmpty) None else Some(bucket)
    new ModelAndView(new RedirectView("/admin/admin.html?item=S3"))
  }
}