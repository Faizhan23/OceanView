package servlet;

import model.Reservation;
import util.FileStorage;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Calculates and displays the bill for a reservation.
 *
 * GET  /BillServlet             → forwards to bill.jsp (search form)
 * POST /BillServlet             → looks up reservation and forwards with bill data
 * GET  /BillServlet?resNo=...   → direct link from view page
 */
@WebServlet("/BillServlet")
public class BillServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/LoginServlet");
            return;
        }

        // Allow direct linking with ?resNo=RES-0001
        String resNo = req.getParameter("resNo");
        if (resNo != null && !resNo.trim().isEmpty()) {
            loadBill(resNo.trim(), req);
        }

        req.getRequestDispatcher("/jsp/bill.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/LoginServlet");
            return;
        }

        String resNo = req.getParameter("reservationNumber");
        if (resNo == null || resNo.trim().isEmpty()) {
            req.setAttribute("billError", "Please enter a reservation number.");
        } else {
            loadBill(resNo.trim(), req);
        }

        req.getRequestDispatcher("/jsp/bill.jsp").forward(req, resp);
    }

    private void loadBill(String resNo, HttpServletRequest req) {
        try {
            Reservation r = FileStorage.findByReservationNumber(resNo);
            if (r != null) {
                req.setAttribute("reservation", r);
                req.setAttribute("numberOfNights", r.getNumberOfNights());
                req.setAttribute("nightlyRate",    r.getNightlyRate());
                req.setAttribute("totalAmount",    r.getTotalAmount());
            } else {
                req.setAttribute("billError",
                    "No reservation found with number: " + resNo);
            }
        } catch (Exception e) {
            getServletContext().log("Error loading bill", e);
            req.setAttribute("billError", "An error occurred. Please try again.");
        }
        req.setAttribute("searchedResNo", resNo);
    }

    private boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("loggedInUser") != null;
    }
}
