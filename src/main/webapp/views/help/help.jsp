<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Help" />
<%@ include file="/views/common/header.jsp" %>

<div class="page-header">
    <h2><i class="fas fa-question-circle"></i> Help & Documentation</h2>
</div>

<div class="help-grid">
    <div class="help-card">
        <h3><i class="fas fa-calendar-plus"></i> Making a Reservation</h3>
        <ol>
            <li>Click <strong>New Reservation</strong> from the Dashboard or Reservations menu.</li>
            <li>Select your <strong>Check-In</strong> and <strong>Check-Out</strong> dates and click <em>Find Rooms</em>.</li>
            <li>Fill in the guest's details (name, address, contact number).</li>
            <li>Select an available room from the dropdown.</li>
            <li>Click <strong>Confirm Reservation</strong>.</li>
        </ol>
        <div class="help-note">
            <i class="fas fa-info-circle"></i>
            The system automatically prevents double bookings. If a room is already reserved for your dates, it will not appear in the list.
        </div>
    </div>

    <div class="help-card">
        <h3><i class="fas fa-receipt"></i> Generating a Bill</h3>
        <ol>
            <li>Navigate to the reservation detail page.</li>
            <li>Click <strong>Generate Bill</strong>.</li>
            <li>Optionally apply a discount amount.</li>
            <li>Click <strong>Print Bill</strong> to print the invoice.</li>
            <li>Click <strong>Mark as Paid</strong> when payment is received.</li>
        </ol>
        <div class="help-note">
            <i class="fas fa-info-circle"></i>
            Bills are computed using a database stored procedure that applies the configured tax rate (10% default).
        </div>
    </div>

    <div class="help-card">
        <h3><i class="fas fa-chart-bar"></i> Reports</h3>
        <ul>
            <li><strong>Monthly Revenue</strong> – Select year and month to view total revenue, room charges, and taxes collected.</li>
            <li><strong>Room Occupancy</strong> – Select a date range to see occupancy percentages for each room.</li>
        </ul>
    </div>

    <div class="help-card">
        <h3><i class="fas fa-search"></i> Searching Reservations</h3>
        <p>Use the search bar on the Reservations page to find bookings by guest name. The search is case-insensitive and supports partial matching.</p>
    </div>

    <div class="help-card">
        <h3><i class="fas fa-times-circle"></i> Cancelling a Reservation</h3>
        <p>Open the reservation details and click <strong>Cancel Reservation</strong>. Cancelled reservations are retained in the system for audit purposes.</p>
    </div>

    <div class="help-card">
        <h3><i class="fas fa-phone"></i> Support</h3>
        <p>For technical support, contact the system administrator at:</p>
        <p><strong>admin@oceanviewresort.com</strong></p>
        <p><strong>+1-800-OCEAN-VW</strong></p>
    </div>
</div>

<%@ include file="/views/common/footer.jsp" %>
