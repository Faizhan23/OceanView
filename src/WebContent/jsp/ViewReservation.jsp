<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.Reservation" %>
<%
    if (session.getAttribute("loggedInUser") == null) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }
    Reservation r     = (Reservation) request.getAttribute("reservation");
    String searchError  = (String)      request.getAttribute("searchError");
    String searchedResNo= (String)      request.getAttribute("searchedResNo");
    if (searchedResNo == null) searchedResNo = "";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>View Reservation – Ocean View Resort</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>

<%@ include file="navbar.jsp" %>

<div class="main-container">
    <div class="page-header">
        <h2>🔍 View Reservation</h2>
        <p>Enter a reservation number to retrieve the booking details.</p>
    </div>

    <%-- Search form --%>
    <div class="card search-card">
        <form action="<%= request.getContextPath() %>/ViewReservationServlet"
              method="post" id="searchForm">
            <div class="search-row">
                <div class="form-group flex-grow">
                    <label for="reservationNumber">Reservation Number</label>
                    <input type="text" id="reservationNumber" name="reservationNumber"
                           value="<%= searchedResNo %>"
                           placeholder="e.g. RES-0001" required
                           style="text-transform:uppercase">
                </div>
                <button type="submit" class="btn btn-primary search-btn">🔍 Search</button>
            </div>
        </form>
    </div>

    <%-- Error --%>
    <% if (searchError != null) { %>
        <div class="alert alert-error">
            <span class="alert-icon">❌</span> <%= searchError %>
        </div>
    <% } %>

    <%-- Result --%>
    <% if (r != null) { %>
        <div class="card detail-card">
            <div class="detail-header">
                <h3>Reservation Details</h3>
                <span class="res-badge"><%= r.getReservationNumber() %></span>
            </div>

            <div class="detail-grid">
                <div class="detail-row">
                    <span class="detail-label">Reservation No.</span>
                    <span class="detail-value"><strong><%= r.getReservationNumber() %></strong></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Guest Name</span>
                    <span class="detail-value"><%= r.getGuestName() %></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Address</span>
                    <span class="detail-value"><%= r.getAddress() %></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Contact Number</span>
                    <span class="detail-value"><%= r.getContactNumber() %></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Room Type</span>
                    <span class="detail-value room-type-badge room-<%= r.getRoomType().toLowerCase() %>"><%= r.getRoomType() %></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Check-in Date</span>
                    <span class="detail-value"><%= r.getCheckInDate() %></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Check-out Date</span>
                    <span class="detail-value"><%= r.getCheckOutDate() %></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Duration</span>
                    <span class="detail-value"><%= r.getNumberOfNights() %> night(s)</span>
                </div>
                <div class="detail-row highlight-row">
                    <span class="detail-label">Estimated Total</span>
                    <span class="detail-value total-amount">LKR <%= String.format("%,.2f", r.getTotalAmount()) %></span>
                </div>
            </div>

            <div class="detail-actions">
                <a href="<%= request.getContextPath() %>/BillServlet?resNo=<%= r.getReservationNumber() %>"
                   class="btn btn-primary">💰 Generate Bill</a>
                <a href="<%= request.getContextPath() %>/ViewReservationServlet"
                   class="btn btn-secondary">🔄 New Search</a>
            </div>
        </div>
    <% } %>
</div>

<%@ include file="footer.jsp" %>

<script>
    document.getElementById('searchForm').addEventListener('submit', function(e) {
        const val = document.getElementById('reservationNumber').value.trim();
        if (!val) {
            e.preventDefault();
            alert('Please enter a reservation number to search.');
        }
    });
</script>
</body>
</html>
