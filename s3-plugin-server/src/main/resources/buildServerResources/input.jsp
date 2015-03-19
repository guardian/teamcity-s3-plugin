<%@ include file="/include.jsp" %>

<form action="/app/s3/config" method="post">
    <table>
        <tbody>
            <tr>
                <td>
                    <label for="bucket">Bucket</label>
                </td><td>
                    <input type="text" id="bucket" name="bucketName" value="${bucketName}" class="longField">
                </td>
            </tr><tr>
                <td>
                    <label for="access-key">AWS Access Key</label>
                </td><td>
                    <input type="text" id="access-key" name="${accessKey}" value="" class="longField">
                    <div class="grayNote">
                        If either the AWS Access Key or AWS Secret Key are not specified, teamcity will fall back to using the
                        <a href="http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/DefaultAWSCredentialsProviderChain.html">
                            DefaultAWSCredentialsProviderChain
                        </a>
                        for authentication.
                    </div>
                </td>
            </tr><tr>
                <td>
                    <label for="secret-key">AWS Secret Key</label>
                </td><td>
                    <input type="text" id="secret-key" name="secretKey" value="${secretKey}" class="longField">
                </td>
            </tr>
        </tbody>
    </table>
    <input type="submit" class="btn btn-default" value="Submit">
</form>