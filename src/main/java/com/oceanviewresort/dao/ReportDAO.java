package com.oceanviewresort.dao;

import java.sql.*;
import java.util.*;
import java.util.logging.*;
import com.oceanviewresort.util.DatabaseConnection;

/**
 * DAO for report data retrieval using stored procedures.
 */
public class ReportDAO {

    private static final Logger LOGGER = Logger.getLogger(ReportDAO.class.getName());

    private static final String CALL_MONTHLY_REVENUE = "{CALL sp_monthly_revenue(?, ?)}";
    private static final String CALL_OCCUPANCY       = "{CALL sp_room_occupancy(?, ?)}";

    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Returns monthly revenue data via stored procedure.
     * Each map contains: period, total_reservations, total_room_charge, total_tax, total_revenue.
     */
    public List<Map<String, Object>> getMonthlyRevenue(int year, int month) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (CallableStatement cs = getConnection().prepareCall(CALL_MONTHLY_REVENUE)) {
            cs.setInt(1, year);
            cs.setInt(2, month);
            try (ResultSet rs = cs.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= colCount; i++) {
                        row.put(meta.getColumnLabel(i), rs.getObject(i));
                    }
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching monthly revenue", e);
        }
        return results;
    }

    /**
     * Returns room occupancy data via stored procedure.
     */
    public List<Map<String, Object>> getRoomOccupancy(String startDate, String endDate) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (CallableStatement cs = getConnection().prepareCall(CALL_OCCUPANCY)) {
            cs.setString(1, startDate);
            cs.setString(2, endDate);
            try (ResultSet rs = cs.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= colCount; i++) {
                        row.put(meta.getColumnLabel(i), rs.getObject(i));
                    }
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching room occupancy", e);
        }
        return results;
    }
}
