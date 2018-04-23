<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
<h2>Hello World!</h2>
<form action="/manage/product/load" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="图片上传"/>
</form>
<form action="/manage/product/richtext_img_upload" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="富文本图片上传"/>
</form>
</body>
</html>
