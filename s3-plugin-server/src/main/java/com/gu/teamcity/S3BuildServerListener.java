package com.gu.teamcity;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jetbrains.buildServer.BuildProblemData;
import jetbrains.buildServer.BuildProblemTypes;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.messages.BuildMessage1;
import jetbrains.buildServer.messages.DefaultMessagesInfo;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.BuildServerListener;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.artifacts.BuildArtifact;
import jetbrains.buildServer.serverSide.artifacts.BuildArtifacts;
import jetbrains.buildServer.serverSide.artifacts.BuildArtifactsViewMode;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class S3BuildServerListener extends BuildServerAdapter {
    private final AmazonS3Client client;

    public S3BuildServerListener(@NotNull EventDispatcher<BuildServerListener> eventDispatcher) {
        client = new AmazonS3Client();
        eventDispatcher.addListener(this);
    }

    @Override
    public void beforeBuildFinish(final SRunningBuild runningBuild) {
        Loggers.SERVER.info("S3 Build finishing soon:" + runningBuild.isArtifactsExists());

        runningBuild.addBuildMessage(new BuildMessage1(DefaultMessagesInfo.SOURCE_ID, DefaultMessagesInfo.MSG_TEXT, Status.NORMAL, new Date(), "About to upload artifacts"));

        if (runningBuild.isArtifactsExists()) {
            final String uploadDirectory = runningBuild.getProjectExternalId() + "/" + runningBuild.getBuildTypeName() + "/" + runningBuild.getBuildNumber();
            normalMessage("Uploading to: " +  uploadDirectory);

            runningBuild.getArtifacts(BuildArtifactsViewMode.VIEW_DEFAULT).iterateArtifacts(new BuildArtifacts.BuildArtifactsProcessor() {
                @NotNull
                public Continuation processBuildArtifact(@NotNull BuildArtifact buildArtifact) {
                    if (buildArtifact.isFile() || buildArtifact.isArchive()) {
                        try {
                            client.putObject("travis-ci-artifact-test", uploadDirectory + "/" + buildArtifact.getName(), buildArtifact.getInputStream(),
                                    new ObjectMetadata());
                            return Continuation.CONTINUE;
                        } catch (IOException e) {
                            runningBuild.addBuildMessage(new BuildMessage1(DefaultMessagesInfo.SOURCE_ID, DefaultMessagesInfo.MSG_BUILD_FAILURE,
                                    Status.ERROR, new Date(), "Error uploading artifacts:" + e.getMessage()));
                            runningBuild.addBuildProblem(BuildProblemData.createBuildProblem("S3 fail", BuildProblemTypes.TC_ERROR_MESSAGE_TYPE, e.getMessage()));
                            e.printStackTrace();
                            return Continuation.BREAK;
                        }
                    } else {
                        runningBuild.addBuildMessage(normalMessage("Found a " + buildArtifact.getRelativePath()));
                        return Continuation.CONTINUE;
                    }
                }
            });

        }

        runningBuild.addBuildMessage(normalMessage("Artifact S3 upload complete"));
    }

    private BuildMessage1 normalMessage(String text) {
        return new BuildMessage1(DefaultMessagesInfo.SOURCE_ID, DefaultMessagesInfo.MSG_TEXT, Status.NORMAL, new Date(), text);
    }
}
