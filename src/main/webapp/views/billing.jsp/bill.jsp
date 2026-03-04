<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Bill" />
<%@ include file="/views/common/header.jsp" %>

<div class="page-header no-print">
    <h2><i class="fas fa-receipt"></i> Bill / Invoice</h2>
    <div class="btn-group">
        <button onclick="window.print()" class="btn btn-primary no-print">
            <i class="fas fa-print"></i> Print Bill
        </button>
        <a href="${pageContext.request.contextPath}/reservation?action=view&id=${bill.reservationId}"
           class="btn btn-outline no-print"><i class="fas fa-arrow-left"></i> Back</a>
    </div>
</div>

<c:if test="${param.msg == 'paid'}">
    <div class="alert alert-success no-print">
        <i class="fas fa-check-circle"></i> Payment recorded successfully!
    </div>
</c:if>

<c:if test="${not empty bill}">
<div class="invoice" id="printable-bill">

    <%-- Invoice Header --%>
    <div class="invoice-header">
        <div class="invoice-brand">
            <h1><i class="fas fa-umbrella-beach"></i> Ocean View Resort</h1>
            <p>Beachfront Paradise | Tel: +1-800-OCEAN-VW | info@oceanviewresort.com</p>
        </div>
        <div class="invoice-meta">
            <h2>INVOICE</h2>
            <table>
                <tr><td>Bill #</td><td>${bill.billId}</td></tr>
                <tr><td>Date</td><td>${bill.generatedAt}</td></tr>
                <tr><td>Status</td>
                    <td>
                        <c:choose>
                            <c:when test="${bill.paid}">
                                <span class="badge badge-confirmed">PAID</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge badge-pending">UNPAID</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </table>
        </div>
    </div>

    <hr class="invoice-divider">

    <%-- Guest & Reservation Info --%>
    <div class="invoice-parties">
        <div class="invoice-to">
            <h4>Bill To:</h4>
            <p><strong>${bill.reservation.guest.guestName}</strong></p>
            <p>${bill.reservation.guest.address}</p>
            <p>${bill.reservation.guest.contactNumber}</p>
            <c:if test="${not empty bill.reservation.guest.email}">
                <p>${bill.reservation.guest.email}</p>
            </c:if>
        </div>
        <div class="reservation-info">
            <h4>Reservation Details:</h4>
            <table>
                <tr><td>Reference</td><td>${bill.reservation.reservationRef}</td></tr>
                <tr><td>Room</td><td>${bill.reservation.room.roomNumber} – ${bill.reservation.room.categoryName}</td></tr>
                <tr><td>Check-In</td><td>${bill.reservation.checkinDate}</td></tr>
                <tr><td>Check-Out</td><td>${bill.reservation.checkoutDate}</td></tr>
                <tr><td>Duration</td><td>${bill.reservation.numNights} night(s)</td></tr>
            </table>
        </div>
    </div>

    <hr class="invoice-divider">

    <%-- Charge Breakdown --%>
    <table class="invoice-table">
        <thead>
            <tr>
                <th>Description</th>
                <th>Rate</th>
                <th>Qty</th>
                <th class="text-right">Amount</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>${bill.reservation.room.categoryName} Room (${bill.reservation.room.roomNumber})</td>
                <td>$<fmt:formatNumber value="${bill.reservation.room.pricePerNight}" pattern="#,##0.00"/>/night</td>
                <td>${bill.reservation.numNights} nights</td>
                <td class="text-right">$<fmt:formatNumber value="${bill.roomCharge}" pattern="#,##0.00"/></td>
            </tr>
            <c:if test="${bill.discount > 0}">
            <tr class="discount-row">
                <td colspan="3">Discount Applied</td>
                <td class="text-right">-$<fmt:formatNumber value="${bill.discount}" pattern="#,##0.00"/></td>
            </tr>
            </c:if>
        </tbody>
        <tfoot>
            <tr>
                <td colspan="3" class="text-right">Subtotal</td>
                <td class="text-right">$<fmt:formatNumber value="${bill.subtotal}" pattern="#,##0.00"/></td>
            </tr>
            <tr>
                <td colspan="3" class="text-right">Tax (${bill.taxRate}%)</td>
                <td class="text-right">$<fmt:formatNumber value="${bill.taxAmount}" pattern="#,##0.00"/></td>
            </tr>
            <tr class="total-row">
                <td colspan="3" class="text-right"><strong>TOTAL</strong></td>
                <td class="text-right">
                    <strong>$<fmt:formatNumber value="${bill.totalAmount}" pattern="#,##0.00"/></strong>
                </td>
            </tr>
        </tfoot>
    </table>

    <%-- Apply Discount Form --%>
    <c:if test="${not bill.paid}">
    <div class="no-print invoice-actions">
        <form action="${pageContext.request.contextPath}/billing" method="get" class="form-inline">
            <input type="hidden" name="reservationId" value="${bill.reservationId}">
            <div class="form-group">
                <label>Apply Discount ($)</label>
                <input type="number" name="discount" class="form-control" min="0"
                       step="0.01" value="${bill.discount}" style="width:120px">
            </div>
            <button type="submit" class="btn btn-secondary">Recalculate</button>
        </form>

        <form action="${pageContext.request.contextPath}/billing" method="post"
              onsubmit="return confirm('Confirm payment received?');">
            <input type="hidden" name="reservationId" value="${bill.reservationId}">
            <input type="hidden" name="action" value="pay">
            <button type="submit" class="btn btn-success btn-lg">
                <i class="fas fa-money-bill-wave"></i> Mark as Paid
            </button>
        </form>
    </div>
    </c:if>

    <div class="invoice-footer">
        <p>Thank you for staying at Ocean View Resort! We look forward to welcoming you again.</p>
        <p><small>This is a computer-generated invoice and does not require a signature.</small></p>
    </div>
</div>
</c:if>

<%@ include file="/views/common/footer.jsp" %>
