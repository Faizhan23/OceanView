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

    private static final String SQL_FIND_BY_RES =
            "SELECT bill_id, reservation_id, room_charge, tax_amount, discount, " +
            "total_amount, tax_rate, is_paid, generated_at, paid_at " +
            "FROM bills WHERE reservation_id = ?";

    private static final String SQL_MARK_PAID =
            "UPDATE bills SET is_paid = 1, paid_at = NOW() WHERE reservation_id = ?";

    public BillDAOImpl() {}

    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Optional<Bill> findByReservationId(int reservationId) {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_FIND_BY_RES)) {
            ps.setInt(1, reservationId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
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

                return Optional.of(b);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding bill for reservation " + reservationId, e);
            return Optional.empty();
        }
    }

    @Override
    public boolean markAsPaid(int reservationId) {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_MARK_PAID)) {
            ps.setInt(1, reservationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error marking bill as paid", e);
            return false;
        }
    }
}