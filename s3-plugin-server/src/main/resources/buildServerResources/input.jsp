<%@ include file="/include.jsp" %>

<form action="/app/s3/config" method="post">
    <div class="form-group">
        <label for="bucket">Bucket</label>
        <input type="text" id="bucket" name="bucketName" value="${bucketName}">
    </div>
    <div class="form-group">
        <label for="access-key">AWS Access Key</label>
        <input type="text" id="access-key" name="accessKey" value="${accessKey}">
    </div>
    <div class="form-group">
        <label for="secret-key">AWS Secret Key</label>
        <input type="text" id="secret-key" name="secretKey" value="${secretKey}">
    </div>
    <input type="submit" class="btn btn-default" value="Submit"/>
</form>