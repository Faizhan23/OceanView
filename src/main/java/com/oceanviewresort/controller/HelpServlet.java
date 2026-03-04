package com.oceanviewresort.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Simple controller for the Help section.
 */
@WebServlet(name = "HelpServlet", urlPatterns = "/help/*")
public class HelpServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/views/help/help.jsp").forward(req, resp);
    }
}
