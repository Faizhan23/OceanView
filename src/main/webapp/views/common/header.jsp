<%-- /views/common/header.jsp --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ocean View Resort – ${pageTitle != null ? pageTitle : 'Dashboard'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<nav class="navbar">
    <div class="navbar-brand">
        <i class="fas fa-umbrella-beach"></i> Ocean View Resort
    </div>
    <c:if test="${sessionScope.loggedInUser != null}">
    <ul class="navbar-menu">
        <li><a href="${pageContext.request.contextPath}/dashboard"><i class="fas fa-tachometer-alt"></i> Dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/reservation"><i class="fas fa-calendar-check"></i> Reservations</a></li>
        <li><a href="${pageContext.request.contextPath}/reports"><i class="fas fa-chart-bar"></i> Reports</a></li>
        <li><a href="${pageContext.request.contextPath}/help"><i class="fas fa-question-circle"></i> Help</a></li>
        <li><a href="${pageContext.request.contextPath}/logout" class="btn-logout"><i class="fas fa-sign-out-alt"></i> Logout (${sessionScope.loggedInUser.username})</a></li>
    </ul>
    </c:if>
</nav>

<main class="container">
<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger"><i class="fas fa-exclamation-triangle"></i> ${errorMessage}</div>
</c:if>
<c:if test="${not empty successMessage}">
    <div class="alert alert-success"><i class="fas fa-check-circle"></i> ${successMessage}</div>
</c:if>
