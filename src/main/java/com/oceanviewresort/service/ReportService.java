// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.service;

import com.oceanviewresort.dao.ReportDAO;
import com.oceanviewresort.util.ValidationUtil;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ReportService {
   private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());
   private final ReportDAO reportDAO;

   public ReportService() {
      this(new ReportDAO());
   }

   public ReportService(ReportDAO reportDAO) {
      this.reportDAO = reportDAO;
   }

   public List<Map<String, Object>> getMonthlyRevenue(int year, int month) {
      if (year >= 2000 && year <= 2100) {
         if (month >= 1 && month <= 12) {
            return this.reportDAO.getMonthlyRevenue(year, month);
         } else {
            throw new IllegalArgumentException("Month must be 1–12.");
         }
      } else {
         throw new IllegalArgumentException("Invalid year.");
      }
   }

   public List<Map<String, Object>> getRoomOccupancy(String startDate, String endDate) {
      if (ValidationUtil.isValidDate(startDate) && ValidationUtil.isValidDate(endDate)) {
         return this.reportDAO.getRoomOccupancy(startDate, endDate);
      } else {
         throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd.");
      }
   }
}
