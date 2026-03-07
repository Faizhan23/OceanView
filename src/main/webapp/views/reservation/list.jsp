<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="All Reservations" />
<%@ include file="/views/common/header.jsp" %>

<div class="page-header">
    <h2><i class="fas fa-list"></i> All Reservations</h2>
    <a href="${pageContext.request.contextPath}/reservation?action=new" class="btn btn-primary">
        <i class="fas fa-plus"></i> New Reservation
    </a>
</div>

<%-- Search Form --%>
<div class="search-bar">
    <form action="${pageContext.request.contextPath}/reservation" method="get" class="form-inline">
        <input type="hidden" name="action" value="search">
        <input type="text" name="q" class="form-control" placeholder="Search by guest name..."
               value="${searchQuery}">
        <button type="submit" class="btn btn-secondary">
            <i class="fas fa-search"></i> Search
        </button>
        <c:if test="${not empty searchQuery}">
            <a href="${pageContext.request.contextPath}/reservation" class="btn btn-outline">Clear</a>
        </c:if>
    </form>
</div>

<div class="panel">
    <div class="table-responsive">
        <table class="table table-striped table-hover">
            <thead>
                <tr>
                    <th>#</th>
                    <th>Reference</th>
                    <th>Guest Name</th>
                    <th>Room</th>
                    <th>Check-In</th>
                    <th>Check-Out</th>
                    <th>Nights</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
            <c:forEach var="res" items="${reservations}" varStatus="loop">
                <tr>
                    <td>${loop.count}</td>
                    <td><strong>${res.reservationRef}</strong></td>
                    <td>${res.guest.guestName}</td>
                    <td>${res.room.roomNumber} (${res.room.categoryName})</td>
                    <td>${res.checkinDate}</td>
                    <td>${res.checkoutDate}</td>
                    <td>${res.numNights}</td>
                    <td>
                        <span class="badge badge-${res.status.name().toLowerCase()}">
                            ${res.status}
                        </span>
                    </td>
                    <td class="action-buttons">
                        <a href="${pageContext.request.contextPath}/reservation?action=view&id=${res.reservationId}"
                           class="btn btn-sm btn-outline" title="View Details">
                            <i class="fas fa-eye"></i>
                        </a>
                        <a href="${pageContext.request.contextPath}/billing?reservationId=${res.reservationId}"
                           class="btn btn-sm btn-success" title="Bill">
                            <i class="fas fa-receipt"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty reservations}">
                <tr>
                    <td colspan="9" class="text-center empty-state">
                        <i class="fas fa-calendar-times fa-3x"></i>
                        <p>No reservations found.</p>
                        <a href="${pageContext.request.contextPath}/reservation?action=new"
                           class="btn btn-primary">Make First Reservation</a>
                    </td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="/views/common/footer.jsp" %>
