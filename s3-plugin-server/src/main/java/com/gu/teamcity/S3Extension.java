package com.gu.teamcity;

import jetbrains.buildServer.serverSide.MainConfigProcessor;
import jetbrains.buildServer.serverSide.SBuildServer;
import org.jdom.Element;

public class S3Extension implements MainConfigProcessor {
    public static final String bucketElement = "bucketElement";
    public static final String s3Element = "S3";

    public String bucketName;

    public S3Extension(SBuildServer server) {
        server.registerExtension(MainConfigProcessor.class, "S3", this);
    }

    public void readFrom(Element root) {
        Element s3 = root.getChild(s3Element);
        if (s3 != null && s3.getChild(bucketElement) != null) {
            bucketName = root.getAttributeValue("name");
        }
    }

    public void writeTo(Element root) {
        if (bucketName != null) {
            Element s3 = new Element(s3Element);
            Element bucket = new Element(bucketElement);
            bucket.setAttribute(bucketElement, bucketName);
            s3.addContent(bucket);
            root.addContent(s3);
        }
    }
}
