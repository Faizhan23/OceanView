<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="javax.tags.core" %>
<%@ taglib prefix="fmt" uri="javax.tags.fmt" %>
<c:set var="pageTitle" value="Dashboard" />
<%@ include file="/views/common/header.jsp" %>

<div class="page-header">
    <h2><i class="fas fa-tachometer-alt"></i> Dashboard</h2>
    <a href="${pageContext.request.contextPath}/reservation?action=new" class="btn btn-primary">
        <i class="fas fa-plus"></i> New Reservation
    </a>
</div>


<div class="stats-grid">
    <div class="stat-card stat-total">
        <div class="stat-icon"><i class="fas fa-calendar-alt"></i></div>
        <div class="stat-body">
            <h3>${totalReservations}</h3>
            <p>Total Reservations</p>
        </div>
    </div>
    <div class="stat-card stat-active">
        <div class="stat-icon"><i class="fas fa-check-circle"></i></div>
        <div class="stat-body">
            <h3>${activeReservations}</h3>
            <p>Active Reservations</p>
        </div>
    </div>
    <div class="stat-card stat-cancelled">
        <div class="stat-icon"><i class="fas fa-times-circle"></i></div>
        <div class="stat-body">
            <h3>${cancelledReservations}</h3>
            <p>Cancelled</p>
        </div>
    </div>
</div>

<div class="quick-links">
    <h3>Quick Actions</h3>
    <div class="quick-grid">
        <a href="${pageContext.request.contextPath}/reservation?action=new" class="quick-card">
            <i class="fas fa-bed fa-2x"></i><p>New Reservation</p>
        </a>
        <a href="${pageContext.request.contextPath}/reservation" class="quick-card">
            <i class="fas fa-list fa-2x"></i><p>View All Reservations</p>
        </a>
        <a href="${pageContext.request.contextPath}/reports?type=revenue" class="quick-card">
            <i class="fas fa-dollar-sign fa-2x"></i><p>Revenue Report</p>
        </a>
        <a href="${pageContext.request.contextPath}/reports?type=occupancy" class="quick-card">
            <i class="fas fa-chart-pie fa-2x"></i><p>Occupancy Report</p>
        </a>
    </div>
</div>

<div class="panel">
    <div class="panel-header">
        <h3><i class="fas fa-history"></i> Recent Reservations</h3>
        <a href="${pageContext.request.contextPath}/reservation" class="btn btn-sm btn-outline">View All</a>
    </div>
    <div class="table-responsive">
        <table class="table">
            <thead>
                <tr>
                    <th>Reference</th>
                    <th>Guest</th>
                    <th>Room</th>
                    <th>Check-In</th>
                    <th>Check-Out</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
            <c:forEach var="res" items="${recentReservations}">
                <tr>
                    <td><strong>${res.reservationRef}</strong></td>
                    <td>${res.guest.guestName}</td>
                    <td>${res.room.roomNumber} – ${res.room.categoryName}</td>
                    <td><fmt:formatDate value="${res.checkinDate}" pattern="dd/MM/yyyy" type="date" /></td>
                    <td><fmt:formatDate value="${res.checkoutDate}" pattern="dd/MM/yyyy" type="date" /></td>
                    <td>
                        <span class="badge badge-${res.status.name().toLowerCase()}">
                            ${res.status}
                        </span>
                    </td>
                    <td>
                        <a href="${pageContext.request.contextPath}/reservation?action=view&id=${res.reservationId}"
                           class="btn btn-sm btn-outline"><i class="fas fa-eye"></i></a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty recentReservations}">
                <tr><td colspan="7" class="text-center">No reservations found.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="/views/common/footer.jsp" %>
