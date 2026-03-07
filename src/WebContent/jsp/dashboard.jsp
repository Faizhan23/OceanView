<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    // Session guard
    if (session.getAttribute("loggedInUser") == null) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }
    String loggedInUser = (String) session.getAttribute("loggedInUser");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard – Ocean View Resort</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>

<%@ include file="navbar.jsp" %>

<div class="main-container">
    <div class="page-header">
        <h2>🏠 Dashboard</h2>
        <p>Welcome back, <strong><%= loggedInUser %></strong>! Manage your hotel reservations below.</p>
    </div>

    <div class="dashboard-grid">

        <a href="<%= request.getContextPath() %>/AddReservationServlet" class="dashboard-card">
            <div class="card-icon">📋</div>
            <h3>New Reservation</h3>
            <p>Add a new guest reservation with room and date details.</p>
        </a>

        <a href="<%= request.getContextPath() %>/ViewReservationServlet" class="dashboard-card">
            <div class="card-icon">🔍</div>
            <h3>View Reservation</h3>
            <p>Search and display full details of an existing reservation.</p>
        </a>

        <a href="<%= request.getContextPath() %>/BillServlet" class="dashboard-card">
            <div class="card-icon">💰</div>
            <h3>Calculate Bill</h3>
            <p>Generate and print the invoice for a completed stay.</p>
        </a>

        <a href="<%= request.getContextPath() %>/jsp/help.jsp" class="dashboard-card">
            <div class="card-icon">❓</div>
            <h3>Help</h3>
            <p>Learn how to use the Ocean View Resort management system.</p>
        </a>

        <a href="<%= request.getContextPath() %>/LogoutServlet" class="dashboard-card card-logout">
            <div class="card-icon">🚪</div>
            <h3>Logout</h3>
            <p>Safely sign out and end your session.</p>
        </a>

    </div>
</div>

<%@ include file="footer.jsp" %>
</body>
</html>
