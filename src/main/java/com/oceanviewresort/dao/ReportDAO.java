// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.dao;

import com.oceanviewresort.model.Bill;
import com.oceanviewresort.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BillDAOImpl implements BillDAO {
   private static final Logger LOGGER = Logger.getLogger(BillDAOImpl.class.getName());
   private static final String SQL_FIND_BY_RES = "SELECT bill_id, reservation_id, room_charge, tax_amount, discount,        total_amount, tax_rate, is_paid, generated_at, paid_at FROM   bills WHERE reservation_id = ?";
   private static final String SQL_MARK_PAID = "UPDATE bills SET is_paid = 1, paid_at = NOW() WHERE reservation_id = ?";

   public BillDAOImpl() {
   }

   private Connection getConnection() {
      return DatabaseConnection.getInstance().getConnection();
   }

   public Optional<Bill> findByReservationId(int reservationId) {
      try {
         Throwable var2 = null;
         Object var3 = null;

         try {
            PreparedStatement ps = this.getConnection().prepareStatement("SELECT bill_id, reservation_id, room_charge, tax_amount, discount,        total_amount, tax_rate, is_paid, generated_at, paid_at FROM   bills WHERE reservation_id = ?");

            Object var10000;
            try {
               ps.setInt(1, reservationId);
               Throwable var5 = null;
               Object var6 = null;

               try {
                  ResultSet rs;
                  var10000 = rs = ps.executeQuery();

                  try {
                     var10000 = rs.next();
                     if (var10000 == false) {
                        return Optional.empty();
                     }

                     Bill b = new Bill();
                     b.setBillId(rs.getInt("bill_id"));
                     b.setReservationId(rs.getInt("reservation_id"));
                     b.setRoomCharge(rs.getDouble("room_charge"));
                     b.setTaxAmount(rs.getDouble("tax_amount"));
                     b.setDiscount(rs.getDouble("discount"));
                     b.setTotalAmount(rs.getDouble("total_amount"));
                     b.setTaxRate(rs.getDouble("tax_rate"));
                     b.setPaid(rs.getBoolean("is_paid"));
                     Timestamp gen = rs.getTimestamp("generated_at");
                     if (gen != null) {
                        b.setGeneratedAt(gen.toLocalDateTime());
                     }

                     Timestamp paid = rs.getTimestamp("paid_at");
                     if (paid != null) {
                        b.setPaidAt(paid.toLocalDateTime());
                     }

                     var10000 = Optional.of(b);
                  } finally {
                     if (rs != null) {
                        rs.close();
                     }

                  }
               } catch (Throwable var32) {
                  if (var5 == null) {
                     var5 = var32;
                  } else if (var5 != var32) {
                     var5.addSuppressed(var32);
                  }

                  throw var5;
               }
            } finally {
               if (ps != null) {
                  ps.close();
               }

            }

            return (Optional<Bill>)var10000;
         } catch (Throwable var34) {
            if (var2 == null) {
               var2 = var34;
            } else if (var2 != var34) {
               var2.addSuppressed(var34);
            }

            throw var2;
         }
      } catch (SQLException e) {
         LOGGER.log(Level.SEVERE, "Error finding bill for reservation " + reservationId, e);
         return Optional.empty();
      }
   }

   public boolean markAsPaid(int reservationId) {
      try {
         Throwable var2 = null;
         Object var3 = null;

         try {
            PreparedStatement ps = this.getConnection().prepareStatement("UPDATE bills SET is_paid = 1, paid_at = NOW() WHERE reservation_id = ?");

            boolean var10000;
            try {
               ps.setInt(1, reservationId);
               var10000 = ps.executeUpdate() > 0;
            } finally {
               if (ps != null) {
                  ps.close();
               }

            }

            return var10000;
         } catch (Throwable var12) {
            if (var2 == null) {
               var2 = var12;
            } else if (var2 != var12) {
               var2.addSuppressed(var12);
            }

            throw var2;
         }
      } catch (SQLException e) {
         LOGGER.log(Level.SEVERE, "Error marking bill as paid", e);
         return false;
      }
   }
}
