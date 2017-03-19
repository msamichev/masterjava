<%--
  Created by IntelliJ IDEA.
  User: msamichev
  Date: 19.03.2017
  Time: 18:41
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Upload File</title>
</head>
<body>
<form method="post" action="upload-file" enctype="multipart/form-data">
    Select file to upload:
    <input type="file" name="uploadFile" />
    <br/><br/>
    <input type="submit" value="Upload" />
</form>
</body>
</html>
