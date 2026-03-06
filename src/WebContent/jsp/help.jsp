<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    if (session.getAttribute("loggedInUser") == null) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Help – Ocean View Resort</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>

<%@ include file="navbar.jsp" %>

<div class="main-container">
    <div class="page-header">
        <h2>❓ Help &amp; User Guide</h2>
        <p>Learn how to use the Ocean View Resort Reservation Management System.</p>
    </div>

    <div class="help-grid">

        <div class="help-card">
            <div class="help-icon">🔐</div>
            <h3>1. Logging In</h3>
            <p>Navigate to the system URL and enter your credentials on the Login page.</p>
            <ul>
                <li>Username: <code>admin</code></li>
                <li>Password: <code>admin123</code></li>
            </ul>
            <p>After successful login you will be redirected to the <strong>Dashboard</strong>. Your session will expire after 30 minutes of inactivity.</p>
        </div>

        <div class="help-card">
            <div class="help-icon">📋</div>
            <h3>2. Adding a New Reservation</h3>
            <p>From the dashboard (or the top navigation) click <strong>New Reservation</strong>.</p>
            <ul>
                <li>The <strong>Reservation Number</strong> is generated automatically.</li>
                <li>Fill in all required fields (marked with <span class="required">*</span>).</li>
                <li>Select a room type and choose check-in / check-out dates.</li>
                <li>Check-out must be after check-in – the form will warn you.</li>
                <li>Click <strong>Save Reservation</strong>. A success banner confirms the save.</li>
            </ul>
        </div>

        <div class="help-card">
            <div class="help-icon">🔍</div>
            <h3>3. Viewing a Reservation</h3>
            <p>Click <strong>View Reservation</strong> in the navigation.</p>
            <ul>
                <li>Type the reservation number (e.g. <code>RES-0001</code>) in the search box.</li>
                <li>Click <strong>Search</strong> to display full guest and booking details.</li>
                <li>If not found, an error message will be shown.</li>
                <li>From the result you can jump directly to <strong>Generate Bill</strong>.</li>
            </ul>
        </div>

        <div class="help-card">
            <div class="help-icon">💰</div>
            <h3>4. Generating a Bill</h3>
            <p>Click <strong>Bill</strong> in the navigation (or the <em>Generate Bill</em> button on the View page).</p>
            <ul>
                <li>Enter the reservation number and click <strong>Get Bill</strong>.</li>
                <li>An itemised invoice shows guest info, nights, rate, and total.</li>
                <li>Click <strong>Print / Save PDF</strong> to print or export the invoice.</li>
            </ul>
            <h4>Room Rates</h4>
            <table class="help-rates-table">
                <tr><th>Room Type</th><th>Rate per Night</th></tr>
                <tr><td>Single</td><td>LKR 5,000</td></tr>
                <tr><td>Double</td><td>LKR 8,000</td></tr>
                <tr><td>Deluxe</td><td>LKR 12,000</td></tr>
            </table>
        </div>

        <div class="help-card">
            <div class="help-icon">🚪</div>
            <h3>5. Logging Out</h3>
            <p>Always log out when you have finished working with the system to protect guest data.</p>
            <ul>
                <li>Click <strong>Logout</strong> in the navigation bar.</li>
                <li>You will be redirected to the Login page.</li>
                <li>Your session is fully invalidated – no cached pages will be accessible.</li>
            </ul>
        </div>

        <div class="help-card">
            <div class="help-icon">💡</div>
            <h3>Tips &amp; Troubleshooting</h3>
            <ul>
                <li>Reservation numbers follow the format <code>RES-XXXX</code> (e.g. <code>RES-0023</code>).</li>
                <li>All reservation data is stored in a text file on the server – no database is required.</li>
                <li>If the session times out, simply log in again; your data is safely saved.</li>
                <li>Contact your system administrator if you forget your login credentials.</li>
            </ul>
        </div>

    </div>
</div>

<%@ include file="footer.jsp" %>
</body>
</html>
