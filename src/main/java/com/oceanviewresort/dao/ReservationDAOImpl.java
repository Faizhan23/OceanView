package com.oceanviewresort.dao;

import com.oceanviewresort.model.*;
import com.oceanviewresort.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC implementation of ReservationDAO.
 * Exclusively uses PreparedStatements; no raw SQL string concatenation.
 */
public class ReservationDAOImpl implements ReservationDAO {

    private static final Logger LOGGER = Logger.getLogger(ReservationDAOImpl.class.getName());

    // ── SQL Statements ─────────────────────────────────────────────────────────
    private static final String SQL_INSERT_GUEST =
            "INSERT INTO guests (guest_name, address, contact_number, email) VALUES (?, ?, ?, ?)";

    private static final String SQL_INSERT_RESERVATION =
            "INSERT INTO reservations " +
            "(reservation_ref, guest_id, room_id, user_id, checkin_date, checkout_date, num_nights, status, special_requests) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_BASE =
            "SELECT r.reservation_id, r.reservation_ref, r.guest_id, r.room_id, r.user_id, " +
            "       r.checkin_date, r.checkout_date, r.num_nights, r.status, r.special_requests, " +
            "       r.created_at, r.updated_at, " +
            "       g.guest_name, g.address, g.contact_number, g.email, " +
            "       rm.room_number, rm.floor_number, rm.capacity, " +
            "       rc.category_id, rc.category_name, rc.price_per_night, rc.description " +
            "FROM   reservations r " +
            "JOIN   guests        g  ON g.guest_id   = r.guest_id " +
            "JOIN   rooms         rm ON rm.room_id   = r.room_id " +
            "JOIN   room_categories rc ON rc.category_id = rm.category_id ";

    private static final String SQL_FIND_BY_ID  = SQL_SELECT_BASE + "WHERE r.reservation_id = ?";
    private static final String SQL_FIND_BY_REF = SQL_SELECT_BASE + "WHERE r.reservation_ref = ?";
    private static final String SQL_FIND_ALL    = SQL_SELECT_BASE + "ORDER BY r.created_at DESC";
    private static final String SQL_FIND_BY_GUEST =
            SQL_SELECT_BASE + "WHERE g.guest_name LIKE ? ORDER BY r.created_at DESC";

    private static final String SQL_AVAILABILITY =
            "SELECT COUNT(*) FROM reservations " +
            "WHERE  room_id = ? AND status NOT IN ('CANCELLED') " +
            "AND    checkin_date < ? AND checkout_date > ?";

    private static final String SQL_AVAILABILITY_EXCL =
            "SELECT COUNT(*) FROM reservations " +
            "WHERE  room_id = ? AND reservation_id != ? AND status NOT IN ('CANCELLED') " +
            "AND    checkin_date < ? AND checkout_date > ?";

    private static final String SQL_UPDATE =
            "UPDATE reservations SET room_id=?, checkin_date=?, checkout_date=?, " +
            "num_nights=?, status=?, special_requests=?, updated_at=NOW() " +
            "WHERE  reservation_id=?";

    private static final String SQL_CANCEL =
            "UPDATE reservations SET status='CANCELLED', updated_at=NOW() WHERE reservation_id=?";

    private static final String SQL_NEXT_SEQ =
            "SELECT COALESCE(MAX(reservation_id), 0) + 1 FROM reservations";

    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

   
    @Override
    public int save(Reservation reservation) {
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);

           
            int guestId = insertGuest(conn, reservation.getGuest());
            reservation.setGuestId(guestId);

        
            try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT_RESERVATION,
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, reservation.getReservationRef());
                ps.setInt   (2, guestId);
                ps.setInt   (3, reservation.getRoomId());
                ps.setInt   (4, reservation.getUserId());
                ps.setDate  (5, Date.valueOf(reservation.getCheckinDate()));
                ps.setDate  (6, Date.valueOf(reservation.getCheckoutDate()));
                ps.setInt   (7, reservation.getNumNights());
                ps.setString(8, reservation.getStatus().name());
                ps.setString(9, reservation.getSpecialRequests());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        int id = keys.getInt(1);
                        reservation.setReservationId(id);
                        conn.commit();
                        return id;
                    }
                }
            }
            conn.rollback();
        } catch (SQLException e) {
            safeRollback(conn);
            LOGGER.log(Level.SEVERE, "Error saving reservation", e);
            // Re-throw as unchecked so service layer can catch and categorise
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            safeSetAutoCommit(conn);
        }
        return -1;
    }

    @Override
    public Optional<Reservation> findById(int id) {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, id);
            return querySingle(ps);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding reservation by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Reservation> findByRef(String ref) {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_FIND_BY_REF)) {
            ps.setString(1, ref);
            return querySingle(ps);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding reservation by ref: " + ref, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Reservation> findAll() {
        List<Reservation> list = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all reservations", e);
        }
        return list;
    }

    @Override
    public List<Reservation> findByGuestName(String name) {
        List<Reservation> list = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_FIND_BY_GUEST)) {
            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding reservations by guest name", e);
        }
        return list;
    }

    @Override
    public boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut) {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_AVAILABILITY)) {
            ps.setInt (1, roomId);
            ps.setDate(2, Date.valueOf(checkOut));
            ps.setDate(3, Date.valueOf(checkIn));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking room availability", e);
        }
        return false;
    }

    @Override
    public boolean isRoomAvailableExcluding(int roomId, LocalDate checkIn, LocalDate checkOut, int excludeId) {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_AVAILABILITY_EXCL)) {
            ps.setInt (1, roomId);
            ps.setInt (2, excludeId);
            ps.setDate(3, Date.valueOf(checkOut));
            ps.setDate(4, Date.valueOf(checkIn));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking room availability (excluding)", e);
        }
        return false;
    }

    @Override
    public boolean update(Reservation r) {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_UPDATE)) {
            ps.setInt   (1, r.getRoomId());
            ps.setDate  (2, Date.valueOf(r.getCheckinDate()));
            ps.setDate  (3, Date.valueOf(r.getCheckoutDate()));
            ps.setInt   (4, r.getNumNights());
            ps.setString(5, r.getStatus().name());
            ps.setString(6, r.getSpecialRequests());
            ps.setInt   (7, r.getReservationId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating reservation", e);
        }
        return false;
    }

    @Override
    public boolean cancel(int id) {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_CANCEL)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error cancelling reservation", e);
        }
        return false;
    }

    @Override
    public int getNextSequence() {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_NEXT_SEQ);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting next sequence", e);
        }
        return 1;
    }

   

    private int insertGuest(Connection conn, Guest guest) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT_GUEST,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, guest.getGuestName());
            ps.setString(2, guest.getAddress());
            ps.setString(3, guest.getContactNumber());
            ps.setString(4, guest.getEmail());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Failed to insert guest record.");
    }

    private Optional<Reservation> querySingle(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    private Reservation mapRow(ResultSet rs) throws SQLException {
     
        Guest g = new Guest();
        g.setGuestId(rs.getInt("guest_id"));
        g.setGuestName(rs.getString("guest_name"));
        g.setAddress(rs.getString("address"));
        g.setContactNumber(rs.getString("contact_number"));
        g.setEmail(rs.getString("email"));

        RoomCategory cat = new RoomCategory();
        cat.setCategoryId(rs.getInt("category_id"));
        cat.setCategoryName(rs.getString("category_name"));
        cat.setPricePerNight(rs.getDouble("price_per_night"));
        cat.setDescription(rs.getString("description"));

        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setCategoryId(rs.getInt("category_id"));
        room.setCategory(cat);
        room.setFloorNumber(rs.getInt("floor_number"));
        room.setCapacity(rs.getInt("capacity"));

   
        Reservation r = new Reservation();
        r.setReservationId(rs.getInt("reservation_id"));
        r.setReservationRef(rs.getString("reservation_ref"));
        r.setGuestId(rs.getInt("guest_id"));
        r.setGuest(g);
        r.setRoomId(rs.getInt("room_id"));
        r.setRoom(room);
        r.setUserId(rs.getInt("user_id"));
        r.setCheckinDate(rs.getDate("checkin_date").toLocalDate());
        r.setCheckoutDate(rs.getDate("checkout_date").toLocalDate());
        r.setNumNights(rs.getInt("num_nights"));
        r.setStatusFromString(rs.getString("status"));
        r.setSpecialRequests(rs.getString("special_requests"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) r.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) r.setUpdatedAt(updatedAt.toLocalDateTime());

        return r;
    }

    private void safeRollback(Connection conn) {
        try { if (conn != null) conn.rollback(); }
        catch (SQLException ignored) { }
    }

    private void safeSetAutoCommit(Connection conn) {
        try { if (conn != null) conn.setAutoCommit(true); }
        catch (SQLException ignored) { }
    }
}
