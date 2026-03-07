<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Monthly Revenue Report" />
<%@ include file="/views/common/header.jsp" %>

<div class="page-header">
    <h2><i class="fas fa-chart-line"></i> Monthly Revenue Report</h2>
    <a href="${pageContext.request.contextPath}/reports?type=occupancy" class="btn btn-outline">
        <i class="fas fa-chart-pie"></i> Occupancy Report
    </a>
</div>

<%-- Filter Form --%>
<div class="panel">
    <form action="${pageContext.request.contextPath}/reports" method="get" class="form-inline">
        <input type="hidden" name="type" value="revenue">
        <div class="form-group">
            <label>Year</label>
            <input type="number" name="year" class="form-control" value="${year}" min="2020" max="2099" style="width:100px">
        </div>
        <div class="form-group">
            <label>Month</label>
            <select name="month" class="form-control">
                <c:forEach begin="1" end="12" var="m">
                    <option value="${m}" ${m == month ? 'selected' : ''}>
                        <fmt:formatDate value="${m}" type="month"/>
                        Month ${m}
                    </option>
                </c:forEach>
            </select>
        </div>
        <button type="submit" class="btn btn-secondary"><i class="fas fa-search"></i> Generate</button>
    </form>
</div>

<c:choose>
<c:when test="${not empty revenueData}">
    <c:forEach var="row" items="${revenueData}">
    <div class="stats-grid">
        <div class="stat-card"><div class="stat-body"><h3>${row['total_reservations']}</h3><p>Total Reservations</p></div></div>
        <div class="stat-card"><div class="stat-body"><h3>$<fmt:formatNumber value="${row['total_room_charge']}" pattern="#,##0.00"/></h3><p>Room Revenue</p></div></div>
        <div class="stat-card"><div class="stat-body"><h3>$<fmt:formatNumber value="${row['total_tax']}" pattern="#,##0.00"/></h3><p>Tax Collected</p></div></div>
        <div class="stat-card stat-active"><div class="stat-body"><h3>$<fmt:formatNumber value="${row['total_revenue']}" pattern="#,##0.00"/></h3><p>Total Revenue</p></div></div>
    </div>
    </c:forEach>
</c:when>
<c:otherwise>
    <div class="alert alert-info">
        <i class="fas fa-info-circle"></i>
        No revenue data found for ${year}/${month}.
    </div>
</c:otherwise>
</c:choose>

<%@ include file="/views/common/footer.jsp" %>
