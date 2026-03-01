package com.oceanviewresort.controller;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Servlet Filter: enforces session-based authentication.
 * Intercepts every request to protected URLs and redirects unauthenticated users to login.
 *
 * Design Pattern: Chain of Responsibility (Filter chain)
 */
@WebFilter(urlPatterns = {"/dashboard/*", "/reservation/*", "/billing/*", "/reports/*", "/help/*"})
public class AuthFilter implements Filter {

    private static final String LOGIN_URL    = "/login";
    private static final String SESSION_USER = "loggedInUser";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession         session = req.getSession(false);

        boolean isLoggedIn = session != null && session.getAttribute(SESSION_USER) != null;

        if (isLoggedIn) {
            chain.doFilter(request, response);
        } else {
            String contextPath = req.getContextPath();
            resp.sendRedirect(contextPath + LOGIN_URL + "?error=session_expired");
        }
    }
}
