package com.oceanviewresort.dao;

import com.oceanviewresort.model.User;
import java.util.Optional;

/**
 * DAO interface for User persistence operations.
 * Follows the DAO Pattern – separates persistence logic from business logic.
 */
public interface UserDAO {

    Optional<User> findByUsername(String username);

    Optional<User> findById(int userId);

    boolean existsByUsername(String username);
}
