package com.oceanviewresort.controller;

import com.oceanviewresort.model.Bill;
import com.oceanviewresort.service.BillingService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Optional;


@WebServlet(name = "BillingServlet", urlPatterns = "/billing/*")
public class BillingServlet extends HttpServlet {

    private BillingService billingService;

    @Override
    public void init() {
        this.billingService = new BillingService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idStr    = req.getParameter("reservationId");
        String discount = req.getParameter("discount");

        try {
            int reservationId = Integer.parseInt(idStr);
            double disc = 0.0;
            if (discount != null && !discount.isBlank()) {
                disc = Double.parseDouble(discount);
            }

            Bill bill = billingService.calculateBill(reservationId, disc);
            req.setAttribute("bill", bill);
            req.getRequestDispatcher("/views/billing/bill.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/reservation");
        } catch (IllegalArgumentException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("/views/billing/bill.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String action = req.getParameter("action");
        String idStr  = req.getParameter("reservationId");
        try {
            int reservationId = Integer.parseInt(idStr);
            if ("pay".equals(action)) {
                billingService.markAsPaid(reservationId);
            }
            resp.sendRedirect(req.getContextPath() + "/billing?reservationId=" + reservationId + "&msg=paid");
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/reservation");
        }
    }
}
