// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.service;

import com.oceanviewresort.dao.UserDAO;
import com.oceanviewresort.dao.UserDAOImpl;
import com.oceanviewresort.model.User;
import com.oceanviewresort.util.PasswordUtil;
import com.oceanviewresort.util.ValidationUtil;
import java.util.Optional;
import java.util.logging.Logger;

public class AuthService {
   private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
   private final UserDAO userDAO;

   public AuthService() {
      this(new UserDAOImpl());
   }

   public AuthService(UserDAO userDAO) {
      this.userDAO = userDAO;
   }

   public Optional<User> authenticate(String username, String password) {
      if (!ValidationUtil.isValidUsername(username)) {
         throw new IllegalArgumentException("Invalid username format.");
      } else if (!ValidationUtil.isValidPassword(password)) {
         throw new IllegalArgumentException("Password must be at least 8 characters.");
      } else {
         Optional<User> userOpt = this.userDAO.findByUsername(username.trim());
         if (userOpt.isEmpty()) {
            LOGGER.warning("Authentication failed – user not found: " + username);
            return Optional.empty();
         } else {
            User user = (User)userOpt.get();
            if (!user.isActive()) {
               LOGGER.warning("Authentication failed – user inactive: " + username);
               return Optional.empty();
            } else if (PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
               LOGGER.info("User authenticated successfully: " + username);
               return Optional.of(user);
            } else {
               LOGGER.warning("Authentication failed – wrong password: " + username);
               return Optional.empty();
            }
         }
      }
   }
}
