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


public class BillingService {

    private static final Logger LOGGER = Logger.getLogger(BillingService.class.getName());


    public static final double DEFAULT_TAX_RATE = 10.0;

    private final BillDAO        billDAO;
    private final ReservationDAO reservationDAO;

   
    public BillingService() {
        this(new BillDAOImpl(), new ReservationDAOImpl());
    }


    public BillingService(BillDAO billDAO, ReservationDAO reservationDAO) {
        this.billDAO        = billDAO;
        this.reservationDAO = reservationDAO;
    }

   
    public Bill calculateBill(int reservationId, double discount) {
        if (discount < 0) {
            throw new IllegalArgumentException("Discount cannot be negative.");
        }

       
        Reservation reservation = reservationDAO.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Reservation not found: " + reservationId));

        Bill bill = callStoredProcedure(reservationId, DEFAULT_TAX_RATE, discount);
        bill.setReservation(reservation);
        return bill;
    }

    
    public Optional<Bill> getBillByReservation(int reservationId) {
        Optional<Bill> billOpt = billDAO.findByReservationId(reservationId);
        if (billOpt.isPresent()) {
            reservationDAO.findById(reservationId)
                    .ifPresent(r -> billOpt.get().setReservation(r));
        }
        return billOpt;
    }

  
    public boolean markAsPaid(int reservationId) {
        return billDAO.markAsPaid(reservationId);
    }

   
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
