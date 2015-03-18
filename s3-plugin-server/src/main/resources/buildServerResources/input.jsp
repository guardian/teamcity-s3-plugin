<%@ include file="/include.jsp" %>

<form action="/app/s3/config" method="post">
    <div class="form-group">
        <label for="bucket">Bucket</label>
        <input type="text" id="bucket" name="bucketName" value="${bucketName}">
    </div>
    <input type="submit" class="btn btn-default" value="Submit"/>
</form>