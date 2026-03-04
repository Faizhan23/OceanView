<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Room Occupancy Report" />
<%@ include file="/views/common/header.jsp" %>

<div class="page-header">
    <h2><i class="fas fa-chart-pie"></i> Room Occupancy Report</h2>
    <a href="${pageContext.request.contextPath}/reports?type=revenue" class="btn btn-outline">
        <i class="fas fa-chart-line"></i> Revenue Report
    </a>
</div>

<div class="panel">
    <form action="${pageContext.request.contextPath}/reports" method="get" class="form-inline">
        <input type="hidden" name="type" value="occupancy">
        <div class="form-group">
            <label>From</label>
            <input type="date" name="start" class="form-control" value="${startDate}">
        </div>
        <div class="form-group">
            <label>To</label>
            <input type="date" name="end" class="form-control" value="${endDate}">
        </div>
        <button type="submit" class="btn btn-secondary"><i class="fas fa-search"></i> Generate</button>
    </form>
</div>

<c:if test="${not empty occupancyData}">
<div class="panel">
    <div class="table-responsive">
        <table class="table">
            <thead>
                <tr>
                    <th>Room</th>
                    <th>Category</th>
                    <th>Times Booked</th>
                    <th>Nights Occupied</th>
                    <th>Occupancy %</th>
                </tr>
            </thead>
            <tbody>
            <c:forEach var="row" items="${occupancyData}">
                <tr>
                    <td><strong>${row['room_number']}</strong></td>
                    <td>${row['category_name']}</td>
                    <td>${row['times_booked']}</td>
                    <td>${row['nights_occupied']}</td>
                    <td>
                        <div class="progress-bar-wrap">
                            <div class="progress-bar" style="width:${row['occupancy_pct']}%"></div>
                            <span>${row['occupancy_pct']}%</span>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
</c:if>

<%@ include file="/views/common/footer.jsp" %>
