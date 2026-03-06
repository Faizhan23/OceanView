// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.controller;

import com.oceanviewresort.model.Reservation;
import com.oceanviewresort.model.Reservation.Status;
import com.oceanviewresort.service.ReservationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(
   name = "DashboardServlet",
   urlPatterns = {"/dashboard"}
)
public class DashboardServlet extends HttpServlet {
   private ReservationService reservationService;

   public DashboardServlet() {
   }

   public void init() {
      this.reservationService = new ReservationService();
   }

   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      List<Reservation> allReservations = this.reservationService.getAllReservations();
      long active = allReservations.stream().filter((r) -> r.getStatus() == Status.CONFIRMED || r.getStatus() == Status.CHECKED_IN).count();
      long cancelled = allReservations.stream().filter((r) -> r.getStatus() == Status.CANCELLED).count();
      req.setAttribute("totalReservations", allReservations.size());
      req.setAttribute("activeReservations", active);
      req.setAttribute("cancelledReservations", cancelled);
      req.setAttribute("recentReservations", allReservations.subList(0, Math.min(5, allReservations.size())));
      req.getRequestDispatcher("/views/admin/dashboard.jsp").forward(req, resp);
   }
}
