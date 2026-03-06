// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.dao;

import com.oceanviewresort.model.User;
import com.oceanviewresort.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAOImpl implements UserDAO {
   private static final Logger LOGGER = Logger.getLogger(UserDAOImpl.class.getName());
   private static final String SQL_FIND_BY_USERNAME = "SELECT u.user_id, u.username, u.password_hash, u.full_name, u.email,        u.role_id, r.role_name, u.is_active, u.created_at FROM   users u JOIN   roles r ON r.role_id = u.role_id WHERE  u.username = ?";
   private static final String SQL_FIND_BY_ID = "SELECT u.user_id, u.username, u.password_hash, u.full_name, u.email,        u.role_id, r.role_name, u.is_active, u.created_at FROM   users u JOIN   roles r ON r.role_id = u.role_id WHERE  u.user_id = ?";
   private static final String SQL_EXISTS_USERNAME = "SELECT COUNT(*) FROM users WHERE username = ?";

   public UserDAOImpl() {
   }

   private Connection getConnection() {
      return DatabaseConnection.getInstance().getConnection();
   }

   public Optional<User> findByUsername(String username) {
      try {
         Throwable var2 = null;
         Object var3 = null;

         try {
            PreparedStatement ps = this.getConnection().prepareStatement("SELECT u.user_id, u.username, u.password_hash, u.full_name, u.email,        u.role_id, r.role_name, u.is_active, u.created_at FROM   users u JOIN   roles r ON r.role_id = u.role_id WHERE  u.username = ?");

            Object var10000;
            try {
               ps.setString(1, username);
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

                     var10000 = Optional.of(this.mapRow(rs));
                  } finally {
                     if (rs != null) {
                        rs.close();
                     }

                  }
               } catch (Throwable var29) {
                  if (var5 == null) {
                     var5 = var29;
                  } else if (var5 != var29) {
                     var5.addSuppressed(var29);
                  }

                  throw var5;
               }
            } finally {
               if (ps != null) {
                  ps.close();
               }

            }

            return (Optional<User>)var10000;
         } catch (Throwable var31) {
            if (var2 == null) {
               var2 = var31;
            } else if (var2 != var31) {
               var2.addSuppressed(var31);
            }

            throw var2;
         }
      } catch (SQLException e) {
         LOGGER.log(Level.SEVERE, "Error finding user by username: " + username, e);
         return Optional.empty();
      }
   }

   public Optional<User> findById(int userId) {
      try {
         Throwable var2 = null;
         Object var3 = null;

         try {
            PreparedStatement ps = this.getConnection().prepareStatement("SELECT u.user_id, u.username, u.password_hash, u.full_name, u.email,        u.role_id, r.role_name, u.is_active, u.created_at FROM   users u JOIN   roles r ON r.role_id = u.role_id WHERE  u.user_id = ?");

            Object var10000;
            try {
               ps.setInt(1, userId);
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

                     var10000 = Optional.of(this.mapRow(rs));
                  } finally {
                     if (rs != null) {
                        rs.close();
                     }

                  }
               } catch (Throwable var29) {
                  if (var5 == null) {
                     var5 = var29;
                  } else if (var5 != var29) {
                     var5.addSuppressed(var29);
                  }

                  throw var5;
               }
            } finally {
               if (ps != null) {
                  ps.close();
               }

            }

            return (Optional<User>)var10000;
         } catch (Throwable var31) {
            if (var2 == null) {
               var2 = var31;
            } else if (var2 != var31) {
               var2.addSuppressed(var31);
            }

            throw var2;
         }
      } catch (SQLException e) {
         LOGGER.log(Level.SEVERE, "Error finding user by id: " + userId, e);
         return Optional.empty();
      }
   }

   public boolean existsByUsername(String username) {
      try {
         Throwable var2 = null;
         Object var3 = null;

         try {
            PreparedStatement ps = this.getConnection().prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?");

            ResultSet var10000;
            try {
               ps.setString(1, username);
               Throwable var5 = null;
               Object var6 = null;

               try {
                  ResultSet rs;
                  var10000 = rs = ps.executeQuery();

                  try {
                     var10000 = rs.next();
                     if (var10000 == false) {
                        return false;
                     }

                     var10000 = rs.getInt(1) > 0;
                  } finally {
                     if (rs != null) {
                        rs.close();
                     }

                  }
               } catch (Throwable var29) {
                  if (var5 == null) {
                     var5 = var29;
                  } else if (var5 != var29) {
                     var5.addSuppressed(var29);
                  }

                  throw var5;
               }
            } finally {
               if (ps != null) {
                  ps.close();
               }

            }

            return (boolean)var10000;
         } catch (Throwable var31) {
            if (var2 == null) {
               var2 = var31;
            } else if (var2 != var31) {
               var2.addSuppressed(var31);
            }

            throw var2;
         }
      } catch (SQLException e) {
         LOGGER.log(Level.SEVERE, "Error checking username existence.", e);
         return false;
      }
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
      if (ts != null) {
         u.setCreatedAt(ts.toLocalDateTime());
      }

      return u;
   }
}
