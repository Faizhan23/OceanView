// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.controller;

import com.oceanviewresort.model.Bill;
import com.oceanviewresort.service.BillingService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
   name = "BillingServlet",
   urlPatterns = {"/billing/*"}
)
public class BillingServlet extends HttpServlet {
   private BillingService billingService;

   public BillingServlet() {
   }

   public void init() {
      this.billingService = new BillingService();
   }

   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      String idStr = req.getParameter("reservationId");
      String discount = req.getParameter("discount");

      try {
         int reservationId = Integer.parseInt(idStr);
         double disc = (double)0.0F;
         if (discount != null && !discount.isBlank()) {
            disc = Double.parseDouble(discount);
         }

         Bill bill = this.billingService.calculateBill(reservationId, disc);
         req.setAttribute("bill", bill);
         req.getRequestDispatcher("/views/billing/bill.jsp").forward(req, resp);
      } catch (NumberFormatException var9) {
         resp.sendRedirect(req.getContextPath() + "/reservation");
      } catch (IllegalArgumentException e) {
         req.setAttribute("errorMessage", e.getMessage());
         req.getRequestDispatcher("/views/billing/bill.jsp").forward(req, resp);
      }

   }

   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      String action = req.getParameter("action");
      String idStr = req.getParameter("reservationId");

      try {
         int reservationId = Integer.parseInt(idStr);
         if ("pay".equals(action)) {
            this.billingService.markAsPaid(reservationId);
         }

         String var10001 = req.getContextPath();
         resp.sendRedirect(var10001 + "/billing?reservationId=" + reservationId + "&msg=paid");
      } catch (NumberFormatException var6) {
         resp.sendRedirect(req.getContextPath() + "/reservation");
      }

   }
}
