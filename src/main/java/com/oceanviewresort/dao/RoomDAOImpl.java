// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.dao;

import com.oceanviewresort.model.Room;
import com.oceanviewresort.model.RoomCategory;
import com.oceanviewresort.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoomDAOImpl implements RoomDAO {
   private static final Logger LOGGER = Logger.getLogger(RoomDAOImpl.class.getName());
   private static final String SQL_ALL_ROOMS = "SELECT rm.room_id, rm.room_number, rm.floor_number, rm.capacity, rm.is_active,        rc.category_id, rc.category_name, rc.price_per_night, rc.description FROM   rooms rm JOIN room_categories rc ON rc.category_id = rm.category_id WHERE  rm.is_active = 1 ORDER BY rm.room_number";
   private static final String SQL_ROOM_BY_ID = "SELECT rm.room_id, rm.room_number, rm.floor_number, rm.capacity, rm.is_active,        rc.category_id, rc.category_name, rc.price_per_night, rc.description FROM   rooms rm JOIN room_categories rc ON rc.category_id = rm.category_id WHERE  rm.room_id = ?";
   private static final String SQL_AVAILABLE_ROOMS = "SELECT rm.room_id, rm.room_number, rm.floor_number, rm.capacity, rm.is_active,        rc.category_id, rc.category_name, rc.price_per_night, rc.description FROM   rooms rm JOIN room_categories rc ON rc.category_id = rm.category_id WHERE  rm.is_active = 1 AND    rm.room_id NOT IN (    SELECT room_id FROM reservations     WHERE  status NOT IN ('CANCELLED')     AND    checkin_date < ? AND checkout_date > ?) ORDER BY rm.room_number";
   private static final String SQL_ALL_CATEGORIES = "SELECT category_id, category_name, price_per_night, description FROM   room_categories ORDER BY price_per_night";

   public RoomDAOImpl() {
   }

   private Connection getConnection() {
      return DatabaseConnection.getInstance().getConnection();
   }

   public List<Room> findAll() {
      List<Room> rooms = new ArrayList();

      try {
         Throwable var2 = null;
         Object var3 = null;

         try {
            PreparedStatement ps = this.getConnection().prepareStatement("SELECT rm.room_id, rm.room_number, rm.floor_number, rm.capacity, rm.is_active,        rc.category_id, rc.category_name, rc.price_per_night, rc.description FROM   rooms rm JOIN room_categories rc ON rc.category_id = rm.category_id WHERE  rm.is_active = 1 ORDER BY rm.room_number");

            try {
               ResultSet rs = ps.executeQuery();

               try {
                  while(rs.next()) {
                     rooms.add(this.mapRow(rs));
                  }
               } finally {
                  if (rs != null) {
                     rs.close();
                  }

               }
            } catch (Throwable var19) {
               if (var2 == null) {
                  var2 = var19;
               } else if (var2 != var19) {
                  var2.addSuppressed(var19);
               }

               if (ps != null) {
                  ps.close();
               }

               throw var2;
            }

            if (ps != null) {
               ps.close();
            }
         } catch (Throwable var20) {
            if (var2 == null) {
               var2 = var20;
            } else if (var2 != var20) {
               var2.addSuppressed(var20);
            }

            throw var2;
         }
      } catch (SQLException e) {
         LOGGER.log(Level.SEVERE, "Error fetching all rooms", e);
      }

      return rooms;
   }

   public Optional<Room> findById(int roomId) {
      try {
         Throwable var2 = null;
         Object var3 = null;

         try {
            PreparedStatement ps = this.getConnection().prepareStatement("SELECT rm.room_id, rm.room_number, rm.floor_number, rm.capacity, rm.is_active,        rc.category_id, rc.category_name, rc.price_per_night, rc.description FROM   rooms rm JOIN room_categories rc ON rc.category_id = rm.category_id WHERE  rm.room_id = ?");

            Object var10000;
            try {
               ps.setInt(1, roomId);
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

            return (Optional<Room>)var10000;
         } catch (Throwable var31) {
            if (var2 == null) {
               var2 = var31;
            } else if (var2 != var31) {
               var2.addSuppressed(var31);
            }

            throw var2;
         }
      } catch (SQLException e) {
         LOGGER.log(Level.SEVERE, "Error finding room by id: " + roomId, e);
         return Optional.empty();
      }
   }

   public List<Room> findAvailableRooms(String checkIn, String checkOut) {
      List<Room> rooms = new ArrayList();

      try {
         Throwable var4 = null;
         Object var5 = null;

         try {
            PreparedStatement ps = this.getConnection().prepareStatement("SELECT rm.room_id, rm.room_number, rm.floor_number, rm.capacity, rm.is_active,        rc.category_id, rc.category_name, rc.price_per_night, rc.description FROM   rooms rm JOIN room_categories rc ON rc.category_id = rm.category_id WHERE  rm.is_active = 1 AND    rm.room_id NOT IN (    SELECT room_id FROM reservations     WHERE  status NOT IN ('CANCELLED')     AND    checkin_date < ? AND checkout_date > ?) ORDER BY rm.room_number");

            try {
               ps.setString(1, checkOut);
               ps.setString(2, checkIn);
               Throwable var7 = null;
               Object var8 = null;

               try {
                  ResultSet rs = ps.executeQuery();

                  try {
                     while(rs.next()) {
                        rooms.add(this.mapRow(rs));
                     }
                  } finally {
                     if (rs != null) {
                        rs.close();
                     }

                  }
               } catch (Throwable var31) {
                  if (var7 == null) {
                     var7 = var31;
                  } else if (var7 != var31) {
                     var7.addSuppressed(var31);
                  }

                  throw var7;
               }
            } finally {
               if (ps != null) {
                  ps.close();
               }

            }
         } catch (Throwable var33) {
            if (var4 == null) {
               var4 = var33;
            } else if (var4 != var33) {
               var4.addSuppressed(var33);
            }

            throw var4;
         }
      } catch (SQLException e) {
         LOGGER.log(Level.SEVERE, "Error finding available rooms", e);
      }

      return rooms;
   }

   public List<RoomCategory> findAllCategories() {
      List<RoomCategory> cats = new ArrayList();

      try {
         Throwable var2 = null;
         Object var3 = null;

         try {
            PreparedStatement ps = this.getConnection().prepareStatement("SELECT category_id, category_name, price_per_night, description FROM   room_categories ORDER BY price_per_night");

            try {
               ResultSet rs = ps.executeQuery();

               try {
                  while(rs.next()) {
                     RoomCategory c = new RoomCategory();
                     c.setCategoryId(rs.getInt("category_id"));
                     c.setCategoryName(rs.getString("category_name"));
                     c.setPricePerNight(rs.getDouble("price_per_night"));
                     c.setDescription(rs.getString("description"));
                     cats.add(c);
                  }
               } finally {
                  if (rs != null) {
                     rs.close();
                  }

               }
            } catch (Throwable var20) {
               if (var2 == null) {
                  var2 = var20;
               } else if (var2 != var20) {
                  var2.addSuppressed(var20);
               }

               if (ps != null) {
                  ps.close();
               }

               throw var2;
            }

            if (ps != null) {
               ps.close();
            }
         } catch (Throwable var21) {
            if (var2 == null) {
               var2 = var21;
            } else if (var2 != var21) {
               var2.addSuppressed(var21);
            }

            throw var2;
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
