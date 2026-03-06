package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Handles user login and logout.
 *
 * GET  /LoginServlet  → forwards to login.jsp
 * POST /LoginServlet  → validates credentials; on success creates session
 *                       and redirects to dashboard.jsp; on failure returns
 *                       to login.jsp with an error message.
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    // Hard-coded credentials for demonstration.
    // In a real system these would be stored (hashed) in a database.
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "admin123";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // If the user is already logged in, skip the login page
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("loggedInUser") != null) {
            resp.sendRedirect(req.getContextPath() + "/jsp/dashboard.jsp");
            return;
        }

        req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password)) {

            // Invalidate any old session to prevent session fixation
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) oldSession.invalidate();

            // Create a new session and store the logged-in user
            HttpSession session = req.getSession(true);
            session.setAttribute("loggedInUser", username);
            session.setMaxInactiveInterval(30 * 60); // 30-minute timeout

            resp.sendRedirect(req.getContextPath() + "/jsp/dashboard.jsp");

        } else {
            // Return to login page with an error message
            req.setAttribute("errorMessage", "Invalid username or password. Please try again.");
            req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
        }
    }
}
