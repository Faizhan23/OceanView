package com.oceanviewresort.controller;

import com.oceanviewresort.model.Reservation;
import com.oceanviewresort.service.ReservationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * Dashboard controller – shows summary statistics for the home screen.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = "/dashboard")
public class DashboardServlet extends HttpServlet {

    private ReservationService reservationService;

    @Override
    public void init() {
        this.reservationService = new ReservationService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<Reservation> allReservations = reservationService.getAllReservations();

        long active    = allReservations.stream()
                .filter(r -> r.getStatus() == Reservation.Status.CONFIRMED
                          || r.getStatus() == Reservation.Status.CHECKED_IN).count();
        long cancelled = allReservations.stream()
                .filter(r -> r.getStatus() == Reservation.Status.CANCELLED).count();

        req.setAttribute("totalReservations", allReservations.size());
        req.setAttribute("activeReservations", active);
        req.setAttribute("cancelledReservations", cancelled);
        req.setAttribute("recentReservations", allReservations.subList(0, Math.min(5, allReservations.size())));

        req.getRequestDispatcher("/views/admin/dashboard.jsp").forward(req, resp);
    }
}
