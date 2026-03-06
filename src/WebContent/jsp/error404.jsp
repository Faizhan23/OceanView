<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>404 – Page Not Found</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<div style="text-align:center; padding: 80px 20px;">
    <div style="font-size:5rem;">🌊</div>
    <h1 style="color:#0a3d62; margin: 16px 0 8px;">404 – Page Not Found</h1>
    <p style="color:#6c757d; margin-bottom: 28px;">The page you are looking for could not be found.</p>
    <a href="<%= request.getContextPath() %>/LoginServlet" class="btn btn-primary">🏠 Back to Login</a>
</div>
</body>
</html>
