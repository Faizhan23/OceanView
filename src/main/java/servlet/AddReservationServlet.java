package servlet;

import model.Reservation;
import util.FileStorage;
import util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Handles the Add New Reservation workflow.
 *
 * GET  /AddReservationServlet  → pre-generates a reservation number and
 *                                forwards to addReservation.jsp
 * POST /AddReservationServlet  → validates input, saves to file,
 *                                and forwards to addReservation.jsp with
 *                                a success or error message.
 */
@WebServlet("/AddReservationServlet")
public class AddReservationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Guard – must be logged in
        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/LoginServlet");
            return;
        }

        try {
            String newNumber = FileStorage.generateReservationNumber();
            req.setAttribute("generatedResNo", newNumber);
        } catch (Exception e) {
            req.setAttribute("generatedResNo", "RES-0001");
            getServletContext().log("Error generating reservation number", e);
        }

        req.getRequestDispatcher("/jsp/addReservation.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Guard – must be logged in
        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/LoginServlet");
            return;
        }

        // Collect form parameters
        String resNumber    = req.getParameter("reservationNumber");
        String guestName    = req.getParameter("guestName");
        String address      = req.getParameter("address");
        String contactNo    = req.getParameter("contactNumber");
        String roomType     = req.getParameter("roomType");
        String checkInStr   = req.getParameter("checkInDate");
        String checkOutStr  = req.getParameter("checkOutDate");

        // Server-side validation
        List<String> errors = ValidationUtil.validateReservation(
                guestName, address, contactNo, roomType, checkInStr, checkOutStr);

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("generatedResNo", resNumber);
            // Re-populate form fields so the user doesn't re-type everything
            req.setAttribute("guestName",   guestName);
            req.setAttribute("address",     address);
            req.setAttribute("contactNo",   contactNo);
            req.setAttribute("roomType",    roomType);
            req.setAttribute("checkInDate", checkInStr);
            req.setAttribute("checkOutDate",checkOutStr);
            req.getRequestDispatcher("/jsp/addReservation.jsp").forward(req, resp);
            return;
        }

        // Check for duplicate reservation number (safety check)
        try {
            if (FileStorage.exists(resNumber)) {
                // Race condition – generate a fresh number
                resNumber = FileStorage.generateReservationNumber();
            }

            Reservation reservation = new Reservation(
                resNumber,
                guestName.trim(),
                address.trim(),
                contactNo.trim(),
                roomType,
                LocalDate.parse(checkInStr.trim()),
                LocalDate.parse(checkOutStr.trim())
            );

            FileStorage.saveReservation(reservation);

            req.setAttribute("successMessage",
                "Reservation " + resNumber + " has been added successfully!");
            req.setAttribute("savedReservation", reservation);

            // Generate next number for a new form entry
            req.setAttribute("generatedResNo", FileStorage.generateReservationNumber());

        } catch (Exception e) {
            getServletContext().log("Error saving reservation", e);
            req.setAttribute("errorMessage", "An error occurred while saving the reservation. Please try again.");
            req.setAttribute("generatedResNo", resNumber);
        }

        req.getRequestDispatcher("/jsp/addReservation.jsp").forward(req, resp);
    }

    private boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("loggedInUser") != null;
    }
}
