<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.Reservation" %>
<%
    if (session.getAttribute("loggedInUser") == null) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }
    Reservation r     = (Reservation) request.getAttribute("reservation");
    Long   nights     = (Long)   request.getAttribute("numberOfNights");
    Double rate       = (Double) request.getAttribute("nightlyRate");
    Double total      = (Double) request.getAttribute("totalAmount");
    String billError  = (String) request.getAttribute("billError");
    String searchedResNo = (String) request.getAttribute("searchedResNo");
    if (searchedResNo == null) searchedResNo = "";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bill – Ocean View Resort</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>

<%@ include file="navbar.jsp" %>

<div class="main-container">
    <div class="page-header no-print">
        <h2>💰 Calculate & Print Bill</h2>
        <p>Enter a reservation number to generate the invoice.</p>
    </div>

    <%-- Search form --%>
    <div class="card search-card no-print">
        <form action="<%= request.getContextPath() %>/BillServlet"
              method="post" id="billSearchForm">
            <div class="search-row">
                <div class="form-group flex-grow">
                    <label for="reservationNumber">Reservation Number</label>
                    <input type="text" id="reservationNumber" name="reservationNumber"
                           value="<%= searchedResNo %>"
                           placeholder="e.g. RES-0001" required>
                </div>
                <button type="submit" class="btn btn-primary search-btn">📄 Get Bill</button>
            </div>
        </form>
    </div>

    <% if (billError != null) { %>
        <div class="alert alert-error no-print">
            <span class="alert-icon">❌</span> <%= billError %>
        </div>
    <% } %>

    <%-- ═══════════════════ PRINTABLE INVOICE ═══════════════════ --%>
    <% if (r != null) { %>
        <div class="invoice-container" id="invoiceArea">

            <div class="invoice-header">
                <div class="hotel-branding">
                    <div class="hotel-logo-print">🌊</div>
                    <div>
                        <h1>Ocean View Resort</h1>
                        <p>No. 42, Marine Drive, Galle, Sri Lanka</p>
                        <p>Tel: +94 91 222 3344 &nbsp;|&nbsp; Email: reservations@oceanviewresort.lk</p>
                    </div>
                </div>
                <div class="invoice-meta">
                    <h2>INVOICE</h2>
                    <table class="meta-table">
                        <tr>
                            <td>Invoice No.:</td>
                            <td><strong>INV-<%= r.getReservationNumber().replace("RES-", "") %></strong></td>
                        </tr>
                        <tr>
                            <td>Reservation No.:</td>
                            <td><%= r.getReservationNumber() %></td>
                        </tr>
                        <tr>
                            <td>Print Date:</td>
                            <td><%= new java.util.Date() %></td>
                        </tr>
                    </table>
                </div>
            </div>

            <hr class="invoice-divider">

            <div class="invoice-section">
                <h3>Guest Information</h3>
                <table class="invoice-table">
                    <tr>
                        <td class="inv-label">Guest Name</td>
                        <td><strong><%= r.getGuestName() %></strong></td>
                    </tr>
                    <tr>
                        <td class="inv-label">Address</td>
                        <td><%= r.getAddress() %></td>
                    </tr>
                    <tr>
                        <td class="inv-label">Contact No.</td>
                        <td><%= r.getContactNumber() %></td>
                    </tr>
                </table>
            </div>

            <div class="invoice-section">
                <h3>Booking Details</h3>
                <table class="invoice-table">
                    <tr>
                        <td class="inv-label">Room Type</td>
                        <td><%= r.getRoomType() %> Room</td>
                    </tr>
                    <tr>
                        <td class="inv-label">Check-in Date</td>
                        <td><%= r.getCheckInDate() %></td>
                    </tr>
                    <tr>
                        <td class="inv-label">Check-out Date</td>
                        <td><%= r.getCheckOutDate() %></td>
                    </tr>
                    <tr>
                        <td class="inv-label">No. of Nights</td>
                        <td><strong><%= nights %> night(s)</strong></td>
                    </tr>
                </table>
            </div>

            <div class="invoice-section">
                <h3>Charges</h3>
                <table class="charges-table">
                    <thead>
                        <tr>
                            <th>Description</th>
                            <th class="text-right">Nights</th>
                            <th class="text-right">Rate (LKR)</th>
                            <th class="text-right">Amount (LKR)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td><%= r.getRoomType() %> Room Accommodation</td>
                            <td class="text-right"><%= nights %></td>
                            <td class="text-right"><%= String.format("%,.2f", rate) %></td>
                            <td class="text-right"><%= String.format("%,.2f", total) %></td>
                        </tr>
                    </tbody>
                    <tfoot>
                        <tr class="subtotal-row">
                            <td colspan="3" class="text-right">Subtotal</td>
                            <td class="text-right">LKR <%= String.format("%,.2f", total) %></td>
                        </tr>
                        <tr class="subtotal-row">
                            <td colspan="3" class="text-right">Taxes &amp; Fees (0%)</td>
                            <td class="text-right">LKR 0.00</td>
                        </tr>
                        <tr class="total-row">
                            <td colspan="3" class="text-right"><strong>TOTAL PAYABLE</strong></td>
                            <td class="text-right total-cell"><strong>LKR <%= String.format("%,.2f", total) %></strong></td>
                        </tr>
                    </tfoot>
                </table>
            </div>

            <div class="invoice-footer">
                <p>Thank you for choosing <strong>Ocean View Resort</strong>. We hope you enjoyed your stay!</p>
                <p class="fine-print">This is a computer-generated invoice. No signature required.</p>
            </div>

        </div><%-- /invoice-container --%>

        <div class="bill-actions no-print">
            <button onclick="printInvoice()" class="btn btn-primary">🖨️ Print / Save PDF</button>
            <a href="<%= request.getContextPath() %>/BillServlet" class="btn btn-secondary">🔄 New Search</a>
            <a href="<%= request.getContextPath() %>/jsp/dashboard.jsp" class="btn btn-secondary">🏠 Dashboard</a>
        </div>
    <% } %>
</div>

<%@ include file="footer.jsp" %>

<script>
    document.getElementById('billSearchForm').addEventListener('submit', function(e) {
        const val = document.getElementById('reservationNumber').value.trim();
        if (!val) {
            e.preventDefault();
            alert('Please enter a reservation number.');
        }
    });

    function printInvoice() {
        window.print();
    }
</script>
</body>
</html>
