<%@ include file="/include.jsp" %>

<form action="/app/s3/config" method="post">
    <table class="runnerFormTable">
        <tbody>
            <tr class="groupingTitle">
                <td colspan="2">S3 Buckets</td>
            </tr><tr>
                <td class="grayNote" colspan="2">
                    No attempt to upload will be made if a bucket is not specified. To upload multiple types to the same
                    bucket, specify the same bucket each time.
                </td>
            </tr>
            <tr>
                <td>
                    <label for="artifactBucket">Artifact Bucket</label>
                </td><td>
                    <input type="text" id="artifactBucket" name="artifactBucket" value="${artifactBucket}" class="longField">
                </td>
            </tr><tr>
                <td>
                    <label for="buildManifestBucket">Build Manifest Bucket</label>
                </td><td>
                    <input type="text" id="buildManifestBucket" name="buildManifestBucket" value="${buildManifestBucket}" class="longField">
                </td>
            </tr><tr>
                <td>
                    <label for="tagManifestBucket">Tag Manifest Bucket</label>
                </td><td>
                    <input type="text" id="tagManifestBucket" name="tagManifestBucket" value="${tagManifestBucket}" class="longField">
                </td>
            </tr><tr class="groupingTitle">
                <td colspan="2">Credentials</td>
            </tr><tr>
                <td class="grayNote" colspan="2">
                    If either the AWS Access Key or AWS Secret Key are not specified, teamcity will fall back to using the
                    <a href="http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/DefaultAWSCredentialsProviderChain.html">
                        DefaultAWSCredentialsProviderChain
                    </a>
                    for authentication.
                </td>
            </tr><tr>
                <td>
                    <label for="access-key">AWS Access Key</label>
                </td><td>
                    <input type="text" id="access-key" name="accessKey" value="${accessKey}" class="longField">
                </td>
            </tr><tr>
                <td>
                    <label for="secret-key">AWS Secret Key</label>
                </td><td>
                    <input type="password" id="secret-key" name="secretKey" class="longField">
                </td>
            </tr><tr>
                <td>
                    <label for="folderPath">Folder path (supports parameters)</label>
                </td><td>
                    <input type="text" id="folderPath" name="folderPath" value="${folderPath}" class="longField">
                </td>
            </tr>
        </tbody>
    </table>
    <input type="submit" class="btn btn-default" value="Submit">
</form>