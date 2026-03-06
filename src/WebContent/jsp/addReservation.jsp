<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, model.Reservation" %>
<%
    if (session.getAttribute("loggedInUser") == null) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }
    String generatedResNo  = (String)   request.getAttribute("generatedResNo");
    String successMessage  = (String)   request.getAttribute("successMessage");
    String errorMessage    = (String)   request.getAttribute("errorMessage");
    List<String> errors    = (List<String>) request.getAttribute("errors");
    Reservation saved      = (Reservation) request.getAttribute("savedReservation");

    // Re-populate values on validation failure
    String rGuestName  = (String) request.getAttribute("guestName");
    String rAddress    = (String) request.getAttribute("address");
    String rContactNo  = (String) request.getAttribute("contactNo");
    String rRoomType   = (String) request.getAttribute("roomType");
    String rCheckIn    = (String) request.getAttribute("checkInDate");
    String rCheckOut   = (String) request.getAttribute("checkOutDate");

    if (rGuestName == null)  rGuestName  = "";
    if (rAddress == null)    rAddress    = "";
    if (rContactNo == null)  rContactNo  = "";
    if (rRoomType == null)   rRoomType   = "";
    if (rCheckIn == null)    rCheckIn    = "";
    if (rCheckOut == null)   rCheckOut   = "";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>New Reservation – Ocean View Resort</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>

<%@ include file="navbar.jsp" %>

<div class="main-container">
    <div class="page-header">
        <h2>📋 Add New Reservation</h2>
        <p>Fill in the guest and booking details below.</p>
    </div>

    <%-- ── Success alert ── --%>
    <% if (successMessage != null) { %>
        <div class="alert alert-success">
            <span class="alert-icon">✅</span> <%= successMessage %>
        </div>
    <% } %>

    <%-- ── Error alert (single) ── --%>
    <% if (errorMessage != null) { %>
        <div class="alert alert-error">
            <span class="alert-icon">❌</span> <%= errorMessage %>
        </div>
    <% } %>

    <%-- ── Validation errors list ── --%>
    <% if (errors != null && !errors.isEmpty()) { %>
        <div class="alert alert-error">
            <span class="alert-icon">⚠️</span> <strong>Please fix the following errors:</strong>
            <ul class="error-list">
                <% for (String err : errors) { %>
                    <li><%= err %></li>
                <% } %>
            </ul>
        </div>
    <% } %>

    <div class="card">
        <form action="<%= request.getContextPath() %>/AddReservationServlet"
              method="post" id="reservationForm" novalidate>

            <div class="form-row">
                <div class="form-group">
                    <label for="reservationNumber">Reservation Number</label>
                    <input type="text" id="reservationNumber" name="reservationNumber"
                           value="<%= generatedResNo != null ? generatedResNo : "" %>"
                           readonly class="readonly-field">
                    <small>Auto-generated – do not edit</small>
                </div>
            </div>

            <div class="form-row two-col">
                <div class="form-group">
                    <label for="guestName">Guest Name <span class="required">*</span></label>
                    <input type="text" id="guestName" name="guestName"
                           value="<%= rGuestName %>"
                           placeholder="e.g. Kasun Perera" required maxlength="100">
                </div>
                <div class="form-group">
                    <label for="contactNumber">Contact Number <span class="required">*</span></label>
                    <input type="tel" id="contactNumber" name="contactNumber"
                           value="<%= rContactNo %>"
                           placeholder="e.g. 0771234567" required
                           pattern="[0-9]{10}" maxlength="10">
                    <small>10-digit Sri Lankan mobile number</small>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="address">Address <span class="required">*</span></label>
                    <textarea id="address" name="address" rows="3"
                              placeholder="Full residential address" required maxlength="300"><%= rAddress %></textarea>
                </div>
            </div>

            <div class="form-row two-col">
                <div class="form-group">
                    <label for="roomType">Room Type <span class="required">*</span></label>
                    <select id="roomType" name="roomType" required>
                        <option value="">-- Select Room Type --</option>
                        <option value="Single"  <%= rRoomType.equals("Single")  ? "selected" : "" %>>Single (LKR 5,000/night)</option>
                        <option value="Double"  <%= rRoomType.equals("Double")  ? "selected" : "" %>>Double (LKR 8,000/night)</option>
                        <option value="Deluxe"  <%= rRoomType.equals("Deluxe")  ? "selected" : "" %>>Deluxe (LKR 12,000/night)</option>
                    </select>
                </div>
            </div>

            <div class="form-row two-col">
                <div class="form-group">
                    <label for="checkInDate">Check-in Date <span class="required">*</span></label>
                    <input type="date" id="checkInDate" name="checkInDate"
                           value="<%= rCheckIn %>" required>
                </div>
                <div class="form-group">
                    <label for="checkOutDate">Check-out Date <span class="required">*</span></label>
                    <input type="date" id="checkOutDate" name="checkOutDate"
                           value="<%= rCheckOut %>" required>
                </div>
            </div>

            <div id="nightsSummary" class="nights-preview" style="display:none;"></div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">💾 Save Reservation</button>
                <a href="<%= request.getContextPath() %>/jsp/dashboard.jsp" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>

<%@ include file="footer.jsp" %>

<script>
    // Set minimum date to today
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('checkInDate').min  = today;
    document.getElementById('checkOutDate').min = today;

    // Live nights preview
    function updateNights() {
        const checkIn  = document.getElementById('checkInDate').value;
        const checkOut = document.getElementById('checkOutDate').value;
        const preview  = document.getElementById('nightsSummary');
        const roomType = document.getElementById('roomType').value;
        const rates    = { Single: 5000, Double: 8000, Deluxe: 12000 };

        if (checkIn && checkOut) {
            const d1 = new Date(checkIn);
            const d2 = new Date(checkOut);
            const nights = Math.round((d2 - d1) / (1000 * 60 * 60 * 24));
            if (nights > 0) {
                const rate  = rates[roomType] || 0;
                const total = nights * rate;
                preview.style.display = 'block';
                preview.innerHTML = `📅 <strong>${nights} night(s)</strong>` +
                    (rate ? ` × LKR ${rate.toLocaleString()} = <strong>LKR ${total.toLocaleString()}</strong>` : '');
            } else {
                preview.style.display = 'none';
            }
        } else {
            preview.style.display = 'none';
        }
    }

    document.getElementById('checkInDate').addEventListener('change', function() {
        // Ensure checkout is always after checkin
        document.getElementById('checkOutDate').min = this.value;
        updateNights();
    });
    document.getElementById('checkOutDate').addEventListener('change', updateNights);
    document.getElementById('roomType').addEventListener('change', updateNights);

    // Client-side form validation
    document.getElementById('reservationForm').addEventListener('submit', function(e) {
        const guestName  = document.getElementById('guestName').value.trim();
        const address    = document.getElementById('address').value.trim();
        const contact    = document.getElementById('contactNumber').value.trim();
        const roomType   = document.getElementById('roomType').value;
        const checkIn    = document.getElementById('checkInDate').value;
        const checkOut   = document.getElementById('checkOutDate').value;
        const messages   = [];

        if (!guestName) messages.push('Guest name is required.');
        if (!address)   messages.push('Address is required.');
        if (!/^[0-9]{10}$/.test(contact)) messages.push('Contact number must be 10 digits.');
        if (!roomType)  messages.push('Please select a room type.');
        if (!checkIn)   messages.push('Check-in date is required.');
        if (!checkOut)  messages.push('Check-out date is required.');
        if (checkIn && checkOut && checkOut <= checkIn)
            messages.push('Check-out date must be after check-in date.');

        if (messages.length > 0) {
            e.preventDefault();
            alert('Please fix the following:\n\n' + messages.join('\n'));
        }
    });
</script>
</body>
</html>
