package com.gu.teamcity

import jetbrains.buildServer.controllers.MultipartFormController
import jetbrains.buildServer.web.openapi.WebControllerManager
import org.jetbrains.annotations.NotNull
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class S3ConfigController(extension: S3ConfigManager, webControllerManager: WebControllerManager) extends MultipartFormController {
  webControllerManager.registerController("/app/s3/**", this)

  protected def doPost(request: HttpServletRequest, response: HttpServletResponse): ModelAndView = {
    def param(name: String) = emptyAsNone(request.getParameter(name))

    extension.update(S3Config(param("bucketName"), param("accessKey"), param("secretKey")))

    new ModelAndView(new RedirectView("/admin/admin.html?item=S3"))
  }

  def emptyAsNone(s: String): Option[String] = if (s == null || s.trim.isEmpty) None else Some(s)
}