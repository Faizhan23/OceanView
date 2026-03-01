package com.oceanviewresort.controller;

import com.oceanviewresort.model.Reservation;
import com.oceanviewresort.model.Room;
import com.oceanviewresort.model.User;
import com.oceanviewresort.service.ReservationService;
import com.oceanviewresort.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * MVC Controller for Reservation operations.
 * Routes:
 *   GET  /reservation            → list all reservations
 *   GET  /reservation?action=new → new reservation form
 *   POST /reservation            → create reservation
 *   GET  /reservation?action=view&id=X  → view details
 *   POST /reservation?action=cancel&id=X → cancel
 *
 * No SQL, no business logic – delegates to ReservationService.
 */
@WebServlet(name = "ReservationServlet", urlPatterns = "/reservation/*")
public class ReservationServlet extends HttpServlet {

    private static final String SESSION_USER = "loggedInUser";
    private ReservationService reservationService;

    @Override
    public void init() {
        this.reservationService = new ReservationService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "new"    -> showNewForm(req, resp);
            case "view"   -> showDetail(req, resp);
            case "search" -> searchReservations(req, resp);
            default       -> listReservations(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if (action == null) action = "create";

        switch (action) {
            case "cancel"  -> cancelReservation(req, resp);
            default        -> createReservation(req, resp);
        }
    }

    // ── Handlers ───────────────────────────────────────────────────────────────

    private void listReservations(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Reservation> reservations = reservationService.getAllReservations();
        req.setAttribute("reservations", reservations);
        req.getRequestDispatcher("/views/reservation/list.jsp").forward(req, resp);
    }

    private void showNewForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String checkIn  = req.getParameter("checkIn");
        String checkOut = req.getParameter("checkOut");

        if (ValidationUtil.isValidDateRange(checkIn, checkOut)) {
            List<Room> availableRooms = reservationService.getAvailableRooms(checkIn, checkOut);
            req.setAttribute("availableRooms", availableRooms);
            req.setAttribute("checkIn",  checkIn);
            req.setAttribute("checkOut", checkOut);
        }
        req.getRequestDispatcher("/views/reservation/form.jsp").forward(req, resp);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idStr = req.getParameter("id");
        try {
            int id = Integer.parseInt(idStr);
            Optional<Reservation> opt = reservationService.getReservationById(id);
            if (opt.isPresent()) {
                req.setAttribute("reservation", opt.get());
                req.getRequestDispatcher("/views/reservation/detail.jsp").forward(req, resp);
            } else {
                req.setAttribute("errorMessage", "Reservation not found.");
                listReservations(req, resp);
            }
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/reservation");
        }
    }

    private void searchReservations(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String query = ValidationUtil.sanitise(req.getParameter("q"));
        List<Reservation> results = reservationService.searchByGuestName(query);
        req.setAttribute("reservations", results);
        req.setAttribute("searchQuery", query);
        req.getRequestDispatcher("/views/reservation/list.jsp").forward(req, resp);
    }

    private void createReservation(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User loggedUser = (User) req.getSession().getAttribute(SESSION_USER);

        try {
            int reservationId = reservationService.createReservation(
                    req.getParameter("guestName"),
                    req.getParameter("address"),
                    req.getParameter("contactNumber"),
                    req.getParameter("email"),
                    Integer.parseInt(req.getParameter("roomId")),
                    req.getParameter("checkIn"),
                    req.getParameter("checkOut"),
                    req.getParameter("specialRequests"),
                    loggedUser.getUserId()
            );
            resp.sendRedirect(req.getContextPath() +
                    "/reservation?action=view&id=" + reservationId + "&msg=created");

        } catch (IllegalArgumentException e) {
            req.setAttribute("errorMessage", e.getMessage());
            // Preserve form data so user can correct inputs
            req.setAttribute("guestName",      req.getParameter("guestName"));
            req.setAttribute("address",        req.getParameter("address"));
            req.setAttribute("contactNumber",  req.getParameter("contactNumber"));
            req.setAttribute("email",          req.getParameter("email"));
            req.setAttribute("checkIn",        req.getParameter("checkIn"));
            req.setAttribute("checkOut",       req.getParameter("checkOut"));
            req.setAttribute("specialRequests",req.getParameter("specialRequests"));
            showNewForm(req, resp);

        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("DOUBLE_BOOKING")) {
                req.setAttribute("errorMessage",
                        "That room is already booked for the selected dates. Please choose different dates or a different room.");
            } else {
                req.setAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            }
            showNewForm(req, resp);
        }
    }

    private void cancelReservation(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            reservationService.cancelReservation(id);
            resp.sendRedirect(req.getContextPath() + "/reservation?msg=cancelled");
        } catch (IllegalArgumentException | IllegalStateException e) {
            req.setAttribute("errorMessage", e.getMessage());
            listReservations(req, resp);
        }
    }
}
