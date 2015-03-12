<%@ include file="/include.jsp" %>

<form action="/app/s3/config" method="post">
    <label for="bucket">Bucket</label>
    <input type="text" name="bucketName" value="${bucketName}">
    <input class="submitButton" id="search" type="submit" value="Submit"/>
</form>