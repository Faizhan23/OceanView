// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.service;

import com.oceanviewresort.dao.BillDAO;
import com.oceanviewresort.dao.BillDAOImpl;
import com.oceanviewresort.dao.ReservationDAO;
import com.oceanviewresort.dao.ReservationDAOImpl;
import com.oceanviewresort.model.Bill;
import com.oceanviewresort.model.Reservation;
import com.oceanviewresort.util.DatabaseConnection;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BillingService {
   private static final Logger LOGGER = Logger.getLogger(BillingService.class.getName());
   public static final double DEFAULT_TAX_RATE = (double)10.0F;
   private final BillDAO billDAO;
   private final ReservationDAO reservationDAO;

   public BillingService() {
      this(new BillDAOImpl(), new ReservationDAOImpl());
   }

   public BillingService(BillDAO billDAO, ReservationDAO reservationDAO) {
      this.billDAO = billDAO;
      this.reservationDAO = reservationDAO;
   }

   public Bill calculateBill(int reservationId, double discount) {
      if (discount < (double)0.0F) {
         throw new IllegalArgumentException("Discount cannot be negative.");
      } else {
         Reservation reservation = (Reservation)this.reservationDAO.findById(reservationId).orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));
         Bill bill = this.callStoredProcedure(reservationId, (double)10.0F, discount);
         bill.setReservation(reservation);
         return bill;
      }
   }

   public Optional<Bill> getBillByReservation(int reservationId) {
      Optional<Bill> billOpt = this.billDAO.findByReservationId(reservationId);
      if (billOpt.isPresent()) {
         this.reservationDAO.findById(reservationId).ifPresent((r) -> ((Bill)billOpt.get()).setReservation(r));
      }

      return billOpt;
   }

   public boolean markAsPaid(int reservationId) {
      return this.billDAO.markAsPaid(reservationId);
   }

   public Bill calculateBillInMemory(int numNights, double pricePerNight, double taxRate, double discount) {
      Bill b = new Bill();
      double roomCharge = (double)numNights * pricePerNight;
      double taxAmount = (roomCharge - discount) * (taxRate / (double)100.0F);
      double total = roomCharge - discount + taxAmount;
      b.setRoomCharge(roomCharge);
      b.setDiscount(discount);
      b.setTaxRate(taxRate);
      b.setTaxAmount(taxAmount);
      b.setTotalAmount(total);
      return b;
   }

   private Bill callStoredProcedure(int reservationId, double taxRate, double discount) {
      String sql = "{CALL sp_calculate_bill(?, ?, ?, ?, ?, ?)}";

      try {
         Throwable var7 = null;
         Object var8 = null;

         try {
            CallableStatement cs = DatabaseConnection.getInstance().getConnection().prepareCall(sql);

            Bill var10000;
            try {
               cs.setInt(1, reservationId);
               cs.setDouble(2, taxRate);
               cs.setDouble(3, discount);
               cs.registerOutParameter(4, 3);
               cs.registerOutParameter(5, 3);
               cs.registerOutParameter(6, 3);
               cs.execute();
               Bill bill = new Bill();
               bill.setReservationId(reservationId);
               bill.setRoomCharge(cs.getDouble(4));
               bill.setTaxAmount(cs.getDouble(5));
               bill.setTotalAmount(cs.getDouble(6));
               bill.setDiscount(discount);
               bill.setTaxRate(taxRate);
               var10000 = bill;
            } finally {
               if (cs != null) {
                  cs.close();
               }

            }

            return var10000;
         } catch (Throwable var18) {
            if (var7 == null) {
               var7 = var18;
            } else if (var7 != var18) {
               var7.addSuppressed(var18);
            }

            throw var7;
         }
      } catch (SQLException e) {
         LOGGER.log(Level.SEVERE, "Error calling sp_calculate_bill", e);
         throw new RuntimeException("Bill calculation failed: " + e.getMessage(), e);
      }
   }
}
