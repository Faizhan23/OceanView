package com.oceanviewresort.controller;

import com.oceanviewresort.service.ReportService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * MVC Controller for reports.
 * Routes:
 *   GET /reports?type=revenue&year=YYYY&month=MM  → monthly revenue
 *   GET /reports?type=occupancy&start=...&end=... → room occupancy
 */
@WebServlet(name = "ReportServlet", urlPatterns = "/reports/*")
public class ReportServlet extends HttpServlet {

    private ReportService reportService;

    @Override
    public void init() {
        this.reportService = new ReportService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String type = req.getParameter("type");

        if ("occupancy".equals(type)) {
            showOccupancyReport(req, resp);
        } else {
            showRevenueReport(req, resp);
        }
    }

    private void showRevenueReport(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        LocalDate now = LocalDate.now();
        int year  = parseIntParam(req.getParameter("year"),  now.getYear());
        int month = parseIntParam(req.getParameter("month"), now.getMonthValue());

        try {
            List<Map<String, Object>> data = reportService.getMonthlyRevenue(year, month);
            req.setAttribute("revenueData", data);
            req.setAttribute("year",  year);
            req.setAttribute("month", month);
        } catch (IllegalArgumentException e) {
            req.setAttribute("errorMessage", e.getMessage());
        }
        req.getRequestDispatcher("/views/reports/revenue.jsp").forward(req, resp);
    }

    private void showOccupancyReport(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        LocalDate now   = LocalDate.now();
        String startDate = req.getParameter("start") != null ? req.getParameter("start")
                : now.withDayOfMonth(1).toString();
        String endDate   = req.getParameter("end") != null ? req.getParameter("end")
                : now.toString();

        try {
            List<Map<String, Object>> data = reportService.getRoomOccupancy(startDate, endDate);
            req.setAttribute("occupancyData", data);
            req.setAttribute("startDate", startDate);
            req.setAttribute("endDate",   endDate);
        } catch (IllegalArgumentException e) {
            req.setAttribute("errorMessage", e.getMessage());
        }
        req.getRequestDispatcher("/views/reports/occupancy.jsp").forward(req, resp);
    }

    private int parseIntParam(String param, int defaultVal) {
        try { return Integer.parseInt(param); }
        catch (Exception e) { return defaultVal; }
    }
}
