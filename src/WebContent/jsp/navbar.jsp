<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String currentUser = (session != null) ? (String) session.getAttribute("loggedInUser") : null;
    String currentPath = request.getRequestURI();
%>
<nav class="navbar">
    <div class="navbar-brand">
        <span class="brand-icon">🌊</span>
        <span>Ocean View Resort</span>
    </div>
    <ul class="navbar-links">
        <li><a href="<%= request.getContextPath() %>/jsp/dashboard.jsp"
               class="<%= currentPath.contains("dashboard") ? "active" : "" %>">🏠 Dashboard</a></li>
        <li><a href="<%= request.getContextPath() %>/AddReservationServlet"
               class="<%= currentPath.contains("AddReservation") ? "active" : "" %>">📋 New Reservation</a></li>
        <li><a href="<%= request.getContextPath() %>/ViewReservationServlet"
               class="<%= currentPath.contains("ViewReservation") ? "active" : "" %>">🔍 View Reservation</a></li>
        <li><a href="<%= request.getContextPath() %>/BillServlet"
               class="<%= currentPath.contains("Bill") ? "active" : "" %>">💰 Bill</a></li>
        <li><a href="<%= request.getContextPath() %>/jsp/help.jsp"
               class="<%= currentPath.contains("help") ? "active" : "" %>">❓ Help</a></li>
        <% if (currentUser != null) { %>
        <li><a href="<%= request.getContextPath() %>/LogoutServlet" class="nav-logout">🚪 Logout (<%= currentUser %>)</a></li>
        <% } %>
    </ul>
</nav>
