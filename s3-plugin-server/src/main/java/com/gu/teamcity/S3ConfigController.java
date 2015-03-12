package com.gu.teamcity;

import jetbrains.buildServer.controllers.MultipartFormController;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class S3ConfigController extends MultipartFormController {
    private S3Extension extension;

    public S3ConfigController(@NotNull S3Extension extension, @NotNull WebControllerManager webControllerManager) {
        this.extension = extension;
        webControllerManager.registerController("/app/s3/**", this);
    }

    @Override
    protected ModelAndView doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String bucket = httpServletRequest.getParameter("bucketName");
        extension.bucketName = bucket;

        return new ModelAndView(new RedirectView("/admin/admin.html?item=S3"));
    }
}
