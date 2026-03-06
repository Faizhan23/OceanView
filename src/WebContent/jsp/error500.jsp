<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>500 – Server Error</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<div style="text-align:center; padding: 80px 20px;">
    <div style="font-size:5rem;">⚠️</div>
    <h1 style="color:#0a3d62; margin: 16px 0 8px;">500 – Internal Server Error</h1>
    <p style="color:#6c757d; margin-bottom: 28px;">Something went wrong on the server. Please try again or contact the administrator.</p>
    <% if (exception != null) { %>
        <p style="color:#922b21; font-size:0.85rem; background:#fadbd8; padding:10px; border-radius:6px; margin-bottom:20px;">
            Error: <%= exception.getMessage() %>
        </p>
    <% } %>
    <a href="<%= request.getContextPath() %>/LoginServlet" class="btn btn-primary">🏠 Back to Login</a>
</div>
</body>
</html>
