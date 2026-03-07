<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ocean View Resort – Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body class="login-page">

<div class="login-container">
    <div class="login-card">
        <div class="login-header">
            <i class="fas fa-umbrella-beach fa-3x"></i>
            <h1>Ocean View Resort</h1>
            <p>Online Room Reservation System</p>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-triangle"></i> ${errorMessage}
            </div>
        </c:if>
        <c:if test="${param.msg == 'logged_out'}">
            <div class="alert alert-info">
                <i class="fas fa-info-circle"></i> You have been logged out successfully.
            </div>
        </c:if>
        <c:if test="${param.error == 'session_expired'}">
            <div class="alert alert-warning">
                <i class="fas fa-clock"></i> Your session has expired. Please log in again.
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post" novalidate>
            <div class="form-group">
                <label for="username"><i class="fas fa-user"></i> Username</label>
                <input type="text" id="username" name="username" class="form-control"
                       value="${fn:escapeXml(username)}" required
                       placeholder="Enter your username" autocomplete="username">
            </div>

            <div class="form-group">
                <label for="password"><i class="fas fa-lock"></i> Password</label>
                <input type="password" id="password" name="password" class="form-control"
                       required placeholder="Enter your password" autocomplete="current-password">
            </div>

            <button type="submit" class="btn btn-primary btn-block">
                <i class="fas fa-sign-in-alt"></i> Login
            </button>
        </form>

        <div class="login-footer">
            <p><small>Default: admin / Admin@1234</small></p>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
