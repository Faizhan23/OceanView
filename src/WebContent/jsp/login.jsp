<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    // Redirect to dashboard if already logged in
    if (session.getAttribute("loggedInUser") != null) {
        response.sendRedirect(request.getContextPath() + "/jsp/dashboard.jsp");
        return;
    }
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ocean View Resort – Login</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body class="login-body">

<div class="login-wrapper">
    <div class="login-card">
        <div class="login-header">
            <div class="hotel-icon">🌊</div>
            <h1>Ocean View Resort</h1>
            <p>Hotel Reservation Management System</p>
            <span class="hotel-location">📍 Galle, Sri Lanka</span>
        </div>

        <% if (errorMessage != null) { %>
            <div class="alert alert-error">
                <span class="alert-icon">⚠️</span> <%= errorMessage %>
            </div>
        <% } %>

        <form action="<%= request.getContextPath() %>/LoginServlet" method="post" novalidate id="loginForm">
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" id="username" name="username"
                       placeholder="Enter username" required autocomplete="username">
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password"
                       placeholder="Enter password" required autocomplete="current-password">
            </div>
            <button type="submit" class="btn btn-primary btn-full">🔐 Sign In</button>
        </form>

        <p class="login-hint">Demo credentials: <strong>admin</strong> / <strong>admin123</strong></p>
    </div>
</div>

<script>
    // Client-side login validation
    document.getElementById('loginForm').addEventListener('submit', function(e) {
        const u = document.getElementById('username').value.trim();
        const p = document.getElementById('password').value.trim();
        if (!u || !p) {
            e.preventDefault();
            alert('Please enter both username and password.');
        }
    });
</script>
</body>
</html>
