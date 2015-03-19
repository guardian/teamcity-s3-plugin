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