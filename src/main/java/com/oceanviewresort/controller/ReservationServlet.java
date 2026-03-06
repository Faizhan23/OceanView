// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.controller;

import com.oceanviewresort.model.Reservation;
import com.oceanviewresort.model.Room;
import com.oceanviewresort.model.User;
import com.oceanviewresort.service.ReservationService;
import com.oceanviewresort.util.ValidationUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(
   name = "ReservationServlet",
   urlPatterns = {"/reservation/*"}
)
public class ReservationServlet extends HttpServlet {
   private static final String SESSION_USER = "loggedInUser";
   private ReservationService reservationService;

   public ReservationServlet() {
   }

   public void init() {
      this.reservationService = new ReservationService();
   }

   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      String action = req.getParameter("action");
      if (action == null) {
         action = "list";
      }

      switch (action) {
         case "search":
            this.searchReservations(req, resp);
            return;
         case "new":
            this.showNewForm(req, resp);
            return;
         case "view":
            this.showDetail(req, resp);
            return;
      }

      this.listReservations(req, resp);
   }

   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      String action = req.getParameter("action");
      if (action == null) {
         action = "create";
      }

      switch (action) {
         case "cancel":
            this.cancelReservation(req, resp);
            break;
         default:
            this.createReservation(req, resp);
      }

   }

   private void listReservations(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      List<Reservation> reservations = this.reservationService.getAllReservations();
      req.setAttribute("reservations", reservations);
      req.getRequestDispatcher("/views/reservation/list.jsp").forward(req, resp);
   }

   private void showNewForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      String checkIn = req.getParameter("checkIn");
      String checkOut = req.getParameter("checkOut");
      if (ValidationUtil.isValidDateRange(checkIn, checkOut)) {
         List<Room> availableRooms = this.reservationService.getAvailableRooms(checkIn, checkOut);
         req.setAttribute("availableRooms", availableRooms);
         req.setAttribute("checkIn", checkIn);
         req.setAttribute("checkOut", checkOut);
      }

      req.getRequestDispatcher("/views/reservation/form.jsp").forward(req, resp);
   }

   private void showDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      String idStr = req.getParameter("id");

      try {
         int id = Integer.parseInt(idStr);
         Optional<Reservation> opt = this.reservationService.getReservationById(id);
         if (opt.isPresent()) {
            req.setAttribute("reservation", opt.get());
            req.getRequestDispatcher("/views/reservation/detail.jsp").forward(req, resp);
         } else {
            req.setAttribute("errorMessage", "Reservation not found.");
            this.listReservations(req, resp);
         }
      } catch (NumberFormatException var6) {
         resp.sendRedirect(req.getContextPath() + "/reservation");
      }

   }

   private void searchReservations(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      String query = ValidationUtil.sanitise(req.getParameter("q"));
      List<Reservation> results = this.reservationService.searchByGuestName(query);
      req.setAttribute("reservations", results);
      req.setAttribute("searchQuery", query);
      req.getRequestDispatcher("/views/reservation/list.jsp").forward(req, resp);
   }

   private void createReservation(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      User loggedUser = (User)req.getSession().getAttribute("loggedInUser");

      try {
         int reservationId = this.reservationService.createReservation(req.getParameter("guestName"), req.getParameter("address"), req.getParameter("contactNumber"), req.getParameter("email"), Integer.parseInt(req.getParameter("roomId")), req.getParameter("checkIn"), req.getParameter("checkOut"), req.getParameter("specialRequests"), loggedUser.getUserId());
         String var10001 = req.getContextPath();
         resp.sendRedirect(var10001 + "/reservation?action=view&id=" + reservationId + "&msg=created");
      } catch (IllegalArgumentException e) {
         req.setAttribute("errorMessage", e.getMessage());
         req.setAttribute("guestName", req.getParameter("guestName"));
         req.setAttribute("address", req.getParameter("address"));
         req.setAttribute("contactNumber", req.getParameter("contactNumber"));
         req.setAttribute("email", req.getParameter("email"));
         req.setAttribute("checkIn", req.getParameter("checkIn"));
         req.setAttribute("checkOut", req.getParameter("checkOut"));
         req.setAttribute("specialRequests", req.getParameter("specialRequests"));
         this.showNewForm(req, resp);
      } catch (RuntimeException e) {
         if (e.getMessage() != null && e.getMessage().contains("DOUBLE_BOOKING")) {
            req.setAttribute("errorMessage", "That room is already booked for the selected dates. Please choose different dates or a different room.");
         } else {
            req.setAttribute("errorMessage", "An unexpected error occurred. Please try again.");
         }

         this.showNewForm(req, resp);
      }

   }

   private void cancelReservation(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
      try {
         int id = Integer.parseInt(req.getParameter("id"));
         this.reservationService.cancelReservation(id);
         resp.sendRedirect(req.getContextPath() + "/reservation?msg=cancelled");
      } catch (IllegalStateException | IllegalArgumentException e) {
         req.setAttribute("errorMessage", ((RuntimeException)e).getMessage());
         this.listReservations(req, resp);
      }

   }
}
