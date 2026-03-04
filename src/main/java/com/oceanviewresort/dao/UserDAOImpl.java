package com.oceanviewresort.dao;

import com.oceanviewresort.model.User;
import com.oceanviewresort.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC implementation of UserDAO.
 * Uses PreparedStatements exclusively – no raw SQL in Servlets.
 */
public class UserDAOImpl implements UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAOImpl.class.getName());

    private static final String SQL_FIND_BY_USERNAME =
            "SELECT u.user_id, u.username, u.password_hash, u.full_name, u.email, " +
            "       u.role_id, r.role_name, u.is_active, u.created_at " +
            "FROM   users u " +
            "JOIN   roles r ON r.role_id = u.role_id " +
            "WHERE  u.username = ?";

    private static final String SQL_FIND_BY_ID =
            "SELECT u.user_id, u.username, u.password_hash, u.full_name, u.email, " +
            "       u.role_id, r.role_name, u.is_active, u.created_at " +
            "FROM   users u " +
            "JOIN   roles r ON r.role_id = u.role_id " +
            "WHERE  u.user_id = ?";

    private static final String SQL_EXISTS_USERNAME =
            "SELECT COUNT(*) FROM users WHERE username = ?";

    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_FIND_BY_USERNAME)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by username: " + username, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(int userId) {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by id: " + userId, e);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsByUsername(String username) {
        try (PreparedStatement ps = getConnection().prepareStatement(SQL_EXISTS_USERNAME)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking username existence.", e);
        }
        return false;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setRoleId(rs.getInt("role_id"));
        u.setRoleName(rs.getString("role_name"));
        u.setActive(rs.getBoolean("is_active"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) u.setCreatedAt(ts.toLocalDateTime());
        return u;
    }
}
