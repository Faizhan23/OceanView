package servlet;

import model.Reservation;
import util.FileStorage;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Handles searching for a reservation by reservation number.
 *
 * GET  /ViewReservationServlet            → forwards to viewReservation.jsp (empty form)
 * GET  /ViewReservationServlet?resNo=...  → searches and forwards with result
 * POST /ViewReservationServlet            → same as GET with query param
 */
@WebServlet("/ViewReservationServlet")
public class ViewReservationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/LoginServlet");
            return;
        }

        String resNo = req.getParameter("resNo");
        if (resNo != null && !resNo.trim().isEmpty()) {
            search(resNo.trim(), req);
        }

        req.getRequestDispatcher("/jsp/viewReservation.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/LoginServlet");
            return;
        }

        String resNo = req.getParameter("reservationNumber");
        if (resNo != null && !resNo.trim().isEmpty()) {
            search(resNo.trim(), req);
        } else {
            req.setAttribute("searchError", "Please enter a reservation number to search.");
        }

        req.getRequestDispatcher("/jsp/viewReservation.jsp").forward(req, resp);
    }

    private void search(String resNo, HttpServletRequest req) {
        try {
            Reservation r = FileStorage.findByReservationNumber(resNo);
            if (r != null) {
                req.setAttribute("reservation", r);
            } else {
                req.setAttribute("searchError",
                    "No reservation found with number: " + resNo);
            }
        } catch (Exception e) {
            getServletContext().log("Error searching reservation", e);
            req.setAttribute("searchError", "An error occurred while searching. Please try again.");
        }
        req.setAttribute("searchedResNo", resNo);
    }

    private boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("loggedInUser") != null;
    }
}
