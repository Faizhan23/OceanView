package com.oceanviewresort.service;

import com.oceanviewresort.dao.UserDAO;
import com.oceanviewresort.dao.UserDAOImpl;
import com.oceanviewresort.model.User;
import com.oceanviewresort.util.PasswordUtil;
import com.oceanviewresort.util.ValidationUtil;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Service class containing authentication business logic.
 * No SQL, no HTTP – pure business rules.
 */
public class AuthService {

    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());

    private final UserDAO userDAO;

    /** Production constructor */
    public AuthService() {
        this(new UserDAOImpl());
    }

    /** Testable constructor – inject mock DAO */
    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Authenticates a user by username and password.
     *
     * @return the authenticated User, or empty if credentials are invalid.
     * @throws IllegalArgumentException if inputs fail validation.
     */
    public Optional<User> authenticate(String username, String password) {
        if (!ValidationUtil.isValidUsername(username)) {
            throw new IllegalArgumentException("Invalid username format.");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
        }

        Optional<User> userOpt = userDAO.findByUsername(username.trim());
        if (userOpt.isEmpty()) {
            LOGGER.warning("Authentication failed – user not found: " + username);
            return Optional.empty();
        }

        User user = userOpt.get();
        if (!user.isActive()) {
            LOGGER.warning("Authentication failed – user inactive: " + username);
            return Optional.empty();
        }

        if (PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            LOGGER.info("User authenticated successfully: " + username);
            return Optional.of(user);
        }

        LOGGER.warning("Authentication failed – wrong password: " + username);
        return Optional.empty();
    }
}
