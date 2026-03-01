package com.oceanviewresort.dao;

import com.oceanviewresort.model.Room;
import com.oceanviewresort.model.RoomCategory;
import com.oceanviewresort.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoomDAOImpl implements RoomDAO {

    private static final Logger LOGGER = Logger.getLogger(RoomDAOImpl.class.getName());

    private static final String SQL_ALL_ROOMS =
            "SELECT rm.room_id, rm.room_number, rm.floor_number, rm.capacity, rm.is_active, " +
            "       rc.category_id, rc.category_name, rc.price_per_night, rc.description " +
            "FROM   rooms rm JOIN room_categories rc ON rc.category_id = rm.category_id " +
            "WHERE  rm.is_active = 1 ORDER BY rm.room_number";

    private static final String SQL_ROOM_BY_ID =
            "SELECT rm.room_id, rm.room_number, rm.floor_number, rm.capacity, rm.is_active, " +
            "       rc.category_id, rc.category_name, rc.price_per_night, rc.description " +
            "FROM   rooms rm JOIN room_categories rc ON rc.category_id = rm.category_id " +
            "WHERE  rm.room_id = ?";

    private static final String SQL_AVAILABLE_ROOMS =
            "SELECT rm.room_id, rm.room_number, rm.floor_number, rm.capacity, rm.is_active, " +
            "       rc.category_id, rc.category_name, rc.price_per_night, rc.description " +
            "FROM   rooms rm JOIN room_categories rc ON rc.category_id = rm.category_id " +
            "WHERE  rm.is_active = 1 " +
            "AND    rm.room_id NOT IN (" +
            "    SELECT room_id FROM reservations " +
            "    WHERE  status NOT IN ('CANCELLED') " +
            "    AND    checkin_date < ? AND checkout_date > ?" +
            ") ORDER BY rm.room_number";

    private static final String SQL_ALL_CATEGORIES =
            "SELECT category_id, category_name, price_per_night, description " +
            "FROM   room_categories ORDER BY price_per_night";

    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_ALL_ROOMS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) rooms.add(mapRow(rs));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all rooms", e);
        }
        return rooms;
    }

    @Override
    public Optional<Room> findById(int roomId) {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_ROOM_BY_ID)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding room by id: " + roomId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Room> findAvailableRooms(String checkIn, String checkOut) {
        List<Room> rooms = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_AVAILABLE_ROOMS)) {
            ps.setString(1, checkOut);
            ps.setString(2, checkIn);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) rooms.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding available rooms", e);
        }
        return rooms;
    }

    @Override
    public List<RoomCategory> findAllCategories() {
        List<RoomCategory> cats = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_ALL_CATEGORIES);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                RoomCategory c = new RoomCategory();
                c.setCategoryId(rs.getInt("category_id"));
                c.setCategoryName(rs.getString("category_name"));
                c.setPricePerNight(rs.getDouble("price_per_night"));
                c.setDescription(rs.getString("description"));
                cats.add(c);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching categories", e);
        }
        return cats;
    }

    private Room mapRow(ResultSet rs) throws SQLException {
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
        room.setActive(rs.getBoolean("is_active"));
        return room;
    }
}
