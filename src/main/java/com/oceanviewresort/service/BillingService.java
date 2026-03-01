package com.oceanviewresort.service;

import com.oceanviewresort.dao.BillDAO;
import com.oceanviewresort.dao.BillDAOImpl;
import com.oceanviewresort.dao.ReservationDAO;
import com.oceanviewresort.dao.ReservationDAOImpl;
import com.oceanviewresort.model.Bill;
import com.oceanviewresort.model.Reservation;
import com.oceanviewresort.util.DatabaseConnection;
import com.oceanviewresort.util.ValidationUtil;

import java.sql.*;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for billing operations.
 * Delegates computation to the MySQL stored procedure sp_calculate_bill.
 *
 * Design Pattern: Demonstrates Factory/Strategy by allowing injected
 * tax strategy (extensible to Factory Pattern for different billing strategies).
 */
public class BillingService {

    private static final Logger LOGGER = Logger.getLogger(BillingService.class.getName());

    /** Standard tax rate applied across the resort. */
    public static final double DEFAULT_TAX_RATE = 10.0;

    private final BillDAO        billDAO;
    private final ReservationDAO reservationDAO;

    /** Production constructor */
    public BillingService() {
        this(new BillDAOImpl(), new ReservationDAOImpl());
    }

    /** Testable constructor – inject mock DAOs */
    public BillingService(BillDAO billDAO, ReservationDAO reservationDAO) {
        this.billDAO        = billDAO;
        this.reservationDAO = reservationDAO;
    }

    /**
     * Calculates (or recalculates) the bill for a given reservation
     * using the MySQL stored procedure sp_calculate_bill.
     *
     * @param reservationId the reservation to bill
     * @param discount      optional discount amount (0 for none)
     * @return the generated Bill object
     */
    public Bill calculateBill(int reservationId, double discount) {
        if (discount < 0) {
            throw new IllegalArgumentException("Discount cannot be negative.");
        }

        // Verify reservation exists
        Reservation reservation = reservationDAO.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Reservation not found: " + reservationId));

        Bill bill = callStoredProcedure(reservationId, DEFAULT_TAX_RATE, discount);
        bill.setReservation(reservation);
        return bill;
    }

    /**
     * Returns a previously calculated bill for a reservation.
     */
    public Optional<Bill> getBillByReservation(int reservationId) {
        Optional<Bill> billOpt = billDAO.findByReservationId(reservationId);
        if (billOpt.isPresent()) {
            reservationDAO.findById(reservationId)
                    .ifPresent(r -> billOpt.get().setReservation(r));
        }
        return billOpt;
    }

    /**
     * Marks a bill as paid.
     */
    public boolean markAsPaid(int reservationId) {
        return billDAO.markAsPaid(reservationId);
    }

    /**
     * Calculates bill purely in Java (no DB) – used in unit tests and as fallback.
     */
    public Bill calculateBillInMemory(int numNights, double pricePerNight,
                                      double taxRate, double discount) {
        Bill b = new Bill();
        double roomCharge = numNights * pricePerNight;
        double taxAmount  = (roomCharge - discount) * (taxRate / 100.0);
        double total      = roomCharge - discount + taxAmount;

        b.setRoomCharge(roomCharge);
        b.setDiscount(discount);
        b.setTaxRate(taxRate);
        b.setTaxAmount(taxAmount);
        b.setTotalAmount(total);
        return b;
    }

    // ── Private: invoke stored procedure ──────────────────────────────────────
    private Bill callStoredProcedure(int reservationId, double taxRate, double discount) {
        String sql = "{CALL sp_calculate_bill(?, ?, ?, ?, ?, ?)}";
        try (CallableStatement cs = DatabaseConnection.getInstance()
                .getConnection().prepareCall(sql)) {

            cs.setInt   (1, reservationId);
            cs.setDouble(2, taxRate);
            cs.setDouble(3, discount);
            cs.registerOutParameter(4, Types.DECIMAL);  
            cs.registerOutParameter(5, Types.DECIMAL);  
            cs.registerOutParameter(6, Types.DECIMAL); 
            cs.execute();

            Bill bill = new Bill();
            bill.setReservationId(reservationId);
            bill.setRoomCharge(cs.getDouble(4));
            bill.setTaxAmount (cs.getDouble(5));
            bill.setTotalAmount(cs.getDouble(6));
            bill.setDiscount(discount);
            bill.setTaxRate(taxRate);
            return bill;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calling sp_calculate_bill", e);
            throw new RuntimeException("Bill calculation failed: " + e.getMessage(), e);
        }
    }
}
