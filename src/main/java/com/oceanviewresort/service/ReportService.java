package com.oceanviewresort.service;

import com.oceanviewresort.dao.ReportDAO;
import com.oceanviewresort.util.ValidationUtil;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Service class for report generation.
 */
public class ReportService {

    private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());

    private final ReportDAO reportDAO;

    public ReportService() {
        this(new ReportDAO());
    }

    public ReportService(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    /**
     * Returns monthly revenue report data.
     * @param year  calendar year (e.g. 2025)
     * @param month month number 1–12
     */
    public List<Map<String, Object>> getMonthlyRevenue(int year, int month) {
        if (year < 2000 || year > 2100) throw new IllegalArgumentException("Invalid year.");
        if (month < 1   || month > 12)  throw new IllegalArgumentException("Month must be 1–12.");
        return reportDAO.getMonthlyRevenue(year, month);
    }

    /**
     * Returns room occupancy report for a date range.
     */
    public List<Map<String, Object>> getRoomOccupancy(String startDate, String endDate) {
        if (!ValidationUtil.isValidDate(startDate) || !ValidationUtil.isValidDate(endDate)) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd.");
        }
        return reportDAO.getRoomOccupancy(startDate, endDate);
    }
}
