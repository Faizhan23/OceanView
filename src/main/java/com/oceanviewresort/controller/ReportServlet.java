// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.controller;

import com.oceanviewresort.service.ReportService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@WebServlet(
   name = "ReportServlet",
   urlPatterns = {"/reports/*"}
)
public class ReportServlet extends HttpServlet {
   private ReportService reportService;

   public ReportServlet() {
   }

   public void init() {
      this.reportService = new ReportService();
   }

   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      String type = req.getParameter("type");
      if ("occupancy".equals(type)) {
         this.showOccupancyReport(req, resp);
      } else {
         this.showRevenueReport(req, resp);
      }

   }

   private void showRevenueReport(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      LocalDate now = LocalDate.now();
      int year = this.parseIntParam(req.getParameter("year"), now.getYear());
      int month = this.parseIntParam(req.getParameter("month"), now.getMonthValue());

      try {
         List<Map<String, Object>> data = this.reportService.getMonthlyRevenue(year, month);
         req.setAttribute("revenueData", data);
         req.setAttribute("year", year);
         req.setAttribute("month", month);
      } catch (IllegalArgumentException e) {
         req.setAttribute("errorMessage", e.getMessage());
      }

      req.getRequestDispatcher("/views/reports/revenue.jsp").forward(req, resp);
   }

   private void showOccupancyReport(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      LocalDate now = LocalDate.now();
      String startDate = req.getParameter("start") != null ? req.getParameter("start") : now.withDayOfMonth(1).toString();
      String endDate = req.getParameter("end") != null ? req.getParameter("end") : now.toString();

      try {
         List<Map<String, Object>> data = this.reportService.getRoomOccupancy(startDate, endDate);
         req.setAttribute("occupancyData", data);
         req.setAttribute("startDate", startDate);
         req.setAttribute("endDate", endDate);
      } catch (IllegalArgumentException e) {
         req.setAttribute("errorMessage", e.getMessage());
      }

      req.getRequestDispatcher("/views/reports/occupancy.jsp").forward(req, resp);
   }

   private int parseIntParam(String param, int defaultVal) {
      try {
         return Integer.parseInt(param);
      } catch (Exception var4) {
         return defaultVal;
      }
   }
}
