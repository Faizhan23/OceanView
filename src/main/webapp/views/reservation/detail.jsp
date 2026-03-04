<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Reservation Details" />
<%@ include file="/views/common/header.jsp" %>

<div class="page-header">
    <h2><i class="fas fa-file-alt"></i> Reservation Details</h2>
    <div class="btn-group">
        <a href="${pageContext.request.contextPath}/billing?reservationId=${reservation.reservationId}"
           class="btn btn-success"><i class="fas fa-receipt"></i> Generate Bill</a>
        <a href="${pageContext.request.contextPath}/reservation" class="btn btn-outline">
            <i class="fas fa-arrow-left"></i> Back
        </a>
    </div>
</div>

<c:if test="${param.msg == 'created'}">
    <div class="alert alert-success">
        <i class="fas fa-check-circle"></i> Reservation created successfully!
    </div>
</c:if>

<div class="detail-grid">
    <%-- Reservation Info --%>
    <div class="detail-panel">
        <div class="detail-panel-header">
            <h3><i class="fas fa-calendar"></i> Reservation Information</h3>
            <span class="badge badge-${reservation.status.name().toLowerCase()}">${reservation.status}</span>
        </div>
        <table class="detail-table">
            <tr><td>Reference</td><td><strong>${reservation.reservationRef}</strong></td></tr>
            <tr><td>Check-In</td><td>${reservation.checkinDate}</td></tr>
            <tr><td>Check-Out</td><td>${reservation.checkoutDate}</td></tr>
            <tr><td>Nights</td><td>${reservation.numNights}</td></tr>
            <tr><td>Created At</td><td>${reservation.createdAt}</td></tr>
            <c:if test="${not empty reservation.specialRequests}">
            <tr><td>Special Requests</td><td>${reservation.specialRequests}</td></tr>
            </c:if>
        </table>
    </div>

    <%-- Guest Info --%>
    <div class="detail-panel">
        <div class="detail-panel-header">
            <h3><i class="fas fa-user"></i> Guest Information</h3>
        </div>
        <table class="detail-table">
            <tr><td>Name</td><td><strong>${reservation.guest.guestName}</strong></td></tr>
            <tr><td>Address</td><td>${reservation.guest.address}</td></tr>
            <tr><td>Contact</td><td>${reservation.guest.contactNumber}</td></tr>
            <c:if test="${not empty reservation.guest.email}">
            <tr><td>Email</td><td>${reservation.guest.email}</td></tr>
            </c:if>
        </table>
    </div>

    <%-- Room Info --%>
    <div class="detail-panel">
        <div class="detail-panel-header">
            <h3><i class="fas fa-bed"></i> Room Information</h3>
        </div>
        <table class="detail-table">
            <tr><td>Room Number</td><td><strong>${reservation.room.roomNumber}</strong></td></tr>
            <tr><td>Category</td><td>${reservation.room.categoryName}</td></tr>
            <tr><td>Floor</td><td>${reservation.room.floorNumber}</td></tr>
            <tr><td>Price/Night</td>
                <td>$<fmt:formatNumber value="${reservation.room.pricePerNight}" pattern="#,##0.00"/></td></tr>
            <tr><td>Est. Room Charge</td>
                <td><strong>$<fmt:formatNumber value="${reservation.estimatedRoomCharge()}" pattern="#,##0.00"/></strong></td></tr>
        </table>
    </div>
</div>

<%-- Actions --%>
<c:if test="${reservation.status != 'CANCELLED'}">
<div class="panel">
    <div class="panel-header"><h3>Actions</h3></div>
    <div class="form-actions">
        <a href="${pageContext.request.contextPath}/billing?reservationId=${reservation.reservationId}"
           class="btn btn-success btn-lg">
            <i class="fas fa-receipt"></i> Calculate & Print Bill
        </a>

        <form action="${pageContext.request.contextPath}/reservation" method="post"
              style="display:inline;"
              onsubmit="return confirm('Are you sure you want to cancel this reservation?');">
            <input type="hidden" name="action" value="cancel">
            <input type="hidden" name="id"     value="${reservation.reservationId}">
            <button type="submit" class="btn btn-danger btn-lg">
                <i class="fas fa-times"></i> Cancel Reservation
            </button>
        </form>
    </div>
</div>
</c:if>

<%@ include file="/views/common/footer.jsp" %>
