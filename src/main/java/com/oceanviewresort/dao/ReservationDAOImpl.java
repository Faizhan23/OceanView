// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.dao;

import com.oceanviewresort.model.Guest;
import com.oceanviewresort.model.Reservation;
import com.oceanviewresort.model.Room;
import com.oceanviewresort.model.RoomCategory;
import com.oceanviewresort.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservationDAOImpl implements ReservationDAO {
   private static final Logger LOGGER = Logger.getLogger(ReservationDAOImpl.class.getName());
   private static final String SQL_INSERT_GUEST = "INSERT INTO guests (guest_name, address, contact_number, email) VALUES (?, ?, ?, ?)";
   private static final String SQL_INSERT_RESERVATION = "INSERT INTO reservations (reservation_ref, guest_id, room_id, user_id, checkin_date, checkout_date, num_nights, status, special_requests) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
   private static final String SQL_SELECT_BASE = "SELECT r.reservation_id, r.reservation_ref, r.guest_id, r.room_id, r.user_id,        r.checkin_date, r.checkout_date, r.num_nights, r.status, r.special_requests,        r.created_at, r.updated_at,        g.guest_name, g.address, g.contact_number, g.email,        rm.room_number, rm.floor_number, rm.capacity,        rc.category_id, rc.category_name, rc.price_per_night, rc.description FROM   reservations r JOIN   guests        g  ON g.guest_id   = r.guest_id JOIN   rooms         rm ON rm.room_id   = r.room_id JOIN   room_categories rc ON rc.category_id = rm.category_id ";
   private static final String SQL_FIND_BY_ID = "SELECT r.reservation_id, r.reservation_ref, r.guest_id, r.room_id, r.user_id,        r.checkin_date, r.checkout_date, r.num_nights, r.status, r.special_requests,        r.created_at, r.updated_at,        g.guest_name, g.address, g.contact_number, g.email,        rm.room_number, rm.floor_number, rm.capacity,        rc.category_id, rc.category_name, rc.price_per_night, rc.description FROM   reservations r JOIN   guests        g  ON g.guest_id   = r.guest_id JOIN   rooms         rm ON rm.room_id   = r.room_id JOIN   room_categories rc ON rc.category_id = rm.category_id WHERE r.reservation_id = ?";
   private static final String SQL_FIND_BY_REF = "SELECT r.reservation_id, r.reservation_ref, r.guest_id, r.room_id, r.user_id,        r.checkin_date, r.checkout_date, r.num_nights, r.status, r.special_requests,        r.created_at, r.updated_at,        g.guest_name, g.address, g.contact_number, g.email,        rm.room_number, rm.floor_number, rm.capacity,        rc.category_id, rc.category_name, rc.price_per_night, rc.description FROM   reservations r JOIN   guests        g  ON g.guest_id   = r.guest_id JOIN   rooms         rm ON rm.room_id   = r.room_id JOIN   room_categories rc ON rc.category_id = rm.category_id WHERE r.reservation_ref = ?";
   private static final String SQL_FIND_ALL = "SELECT r.reservation_id, r.reservation_ref, r.guest_id, r.room_id, r.user_id,        r.checkin_date, r.checkout_date, r.num_nights, r.status, r.special_requests,        r.created_at, r.updated_at,        g.guest_name, g.address, g.contact_number, g.email,        rm.room_number, rm.floor_number, rm.capacity,        rc.category_id, rc.category_name, rc.price_per_night, rc.description FROM   reservations r JOIN   guests        g  ON g.guest_id   = r.guest_id JOIN   rooms         rm ON rm.room_id   = r.room_id JOIN   room_categories rc ON rc.category_id = rm.category_id ORDER BY r.created_at DESC";
   private static final String SQL_FIND_BY_GUEST = "SELECT r.reservation_id, r.reservation_ref, r.guest_id, r.room_id, r.user_id,        r.checkin_date, r.checkout_date, r.num_nights, r.status, r.special_requests,        r.created_at, r.updated_at,        g.guest_name, g.address, g.contact_number, g.email,        rm.room_number, rm.floor_number, rm.capacity,        rc.category_id, rc.category_name, rc.price_per_night, rc.description FROM   reservations r JOIN   guests        g  ON g.guest_id   = r.guest_id JOIN   rooms         rm ON rm.room_id   = r.room_id JOIN   room_categories rc ON rc.category_id = rm.category_id WHERE g.guest_name LIKE ? ORDER BY r.created_at DESC";
   private static final String SQL_AVAILABILITY = "SELECT COUNT(*) FROM reservations WHERE  room_id = ? AND status NOT IN ('CANCELLED') AND    checkin_date < ? AND checkout_date > ?";
   private static final String SQL_AVAILABILITY_EXCL = "SELECT COUNT(*) FROM reservations WHERE  room_id = ? AND reservation_id != ? AND status NOT IN ('CANCELLED') AND    checkin_date < ? AND checkout_date > ?";
   private static final String SQL_UPDATE = "UPDATE reservations SET room_id=?, checkin_date=?, checkout_date=?, num_nights=?, status=?, special_requests=?, updated_at=NOW() WHERE  reservation_id=?";
   private static final String SQL_CANCEL = "UPDATE reservations SET status='CANCELLED', updated_at=NOW() WHERE reservation_id=?";
   private static final String SQL_NEXT_SEQ = "SELECT COALESCE(MAX(reservation_id), 0) + 1 FROM reservations";

   public ReservationDAOImpl() {
   }

   private Connection getConnection() {
      return DatabaseConnection.getInstance().getConnection();
   }

   public int save(Reservation reservation) {
      Connection conn = this.getConnection();

      try {
         conn.setAutoCommit(false);
         int guestId = this.insertGuest(conn, reservation.getGuest());
         reservation.setGuestId(guestId);
         Throwable var4 = null;
         Object var5 = null;

         try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO reservations (reservation_ref, guest_id, room_id, user_id, checkin_date, checkout_date, num_nights, status, special_requests) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);

            try {
               ps.setString(1, reservation.getReservationRef());
               ps.setInt(2, guestId);
               ps.setInt(3, reservation.getRoomId());
               ps.setInt(4, reservation.getUserId());
               ps.setDate(5, Date.valueOf(reservation.getCheckinDate()));
               ps.setDate(6, Date.valueOf(reservation.getCheckoutDate()));
               ps.setInt(7, reservation.getNumNights());
               ps.setString(8, reservation.getStatus().name());
               ps.setString(9, reservation.getSpecialRequests());
               ps.executeUpdate();
               Throwable var7 = null;
               Object var8 = null;

               try {
                  ResultSet keys = ps.getGeneratedKeys();

                  try {
                     if (keys.next()) {
                        int id = keys.getInt(1);
                        reservation.setReservationId(id);
                        conn.commit();
                        int var12 = id;
                        return var12;
                     }
                  } finally {
                     if (keys != null) {
                        keys.close();
                     }

                  }
               } catch (Throwable var44) {
                  if (var7 == null) {
                     var7 = var44;
                  } else if (var7 != var44) {
                     var7.addSuppressed(var44);
                  }

                  throw var7;
               }
            } finally {
               if (ps != null) {
                  ps.close();
               }

            }
         } catch (Throwable var46) {
            if (var4 == null) {
               var4 = var46;
            } else if (var4 != var46) {
               var4.addSuppressed(var46);
            }

            throw var4;
         }

         conn.rollback();
         return -1;
      } catch (SQLException e) {
         this.safeRollback(conn);
         LOGGER.log(Level.SEVERE, "Error saving reservation", e);
         throw new RuntimeException(e.getMessage(), e);
      } finally {
         this.safeSetAutoCommit(conn);
      }
   }

   public Optional<Reservation> findById(int id) {
      try {
         Throwable var2 = null;
         Object var3 = null;

         try {
            PreparedStatement ps = this.getConnection().prepareStatement("SELECT r.reservation_id, r.reservation_ref, r.guest_id, r.room_id, r.user_id,        r.checkin_date, r.checkout_date, r.num_nights, r.status, r.special_requests,        r.created_at, r.updated_at,        g.guest_name, g.address, g.contact_number, g.email,        rm.room_number, rm.floor_number, rm.capacity,        rc.category_id, rc.category_name, rc.price_per_night, rc.description FROM   reservations r JOIN   guests        g  ON g.guest_id   = r.guest_id JOIN   rooms         rm ON rm.room_id   = r.room_id JOIN   room_categories rc ON rc.category_id = rm.category_id WHERE r.reservation_id = ?");

            Optional var10000;
            try {
               ps.setInt(1, id);
               var10000 = this.querySingle(ps);
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
         LOGGER.log(Level.SEVERE, "Error finding reservation by id: " + id, e);
         return Optional.empty();
      }
   }

   public Optional<Reservation> findByRef(String ref) {
      try {
         Throwable var2 = null;
         Object var3 = null;

         try {
            PreparedStatement ps = this.getConnection().prepareStatement("SELECT r.reservation_id, r.reservation_ref, r.guest_id, r.room_id, r.user_id,        r.checkin_date, r.checkout_date, r.num_nights, r.status, r.special_requests,        r.created_at, r.updated_at,        g.guest_name, g.address, g.contact_number, g.email,        rm.room_number, rm.floor_number, rm.capacity,        rc.category_id, rc.category_name, rc.price_per_night, rc.description FROM   reservations r JOIN   guests        g  ON g.guest_id   = r.guest_id JOIN   rooms         rm ON rm.room_id   = r.room_id JOIN   room_categories rc ON rc.category_id = rm.category_id WHERE r.reservation_ref = ?");

            Optional var10000;
            try {
               ps.setString(1, ref);
               var10000 = this.querySingle(ps);
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
         LOGGER.log(Level.SEVERE, "Error finding reservation by ref: " + ref, e);
         return Optional.empty();
      }
   }

   public List<Reservation> findAll() {
      List<Reservation> list = new ArrayList();

      try {
         Throwable var2 = null;
         Object var3 = null;

         try {
            PreparedStatement ps = this.getConnection().prepareStatement("SELECT r.reservation_id, r.reservation_ref, r.guest_id, r.room_id, r.user_id,        r.checkin_date, r.checkout_date, r.num_nights, r.status, r.special_requests,        r.created_at, r.updated_at,        g.guest_name, g.address, g.contact_number, g.email,        rm.room_number, rm.floor_number, rm.capacity,        rc.category_id, rc.category_name, rc.price_per_night, rc.description FROM   reservations r JOIN   guests        g  ON g.guest_id   = r.guest_id JOIN   rooms         rm ON rm.room_id   = r.room_id JOIN   room_categories rc ON rc.category_id = rm.category_id ORDER BY r.created_at DESC");

            try {
               ResultSet rs = ps.executeQuery();

               try {
                  while(rs.next()) {
                     list.add(this.mapRow(rs));
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
         LOGGER.log(Level.SEVERE, "Error fetching all reservations", e);
      }

      return list;
   }

   public List<Reservation> findByGuestName(String name) {
      List<Reservation> list = new ArrayList();

      try {
         Throwable var3 = null;
         Object var4 = null;

         try {
            PreparedStatement ps = this.getConnection().prepareStatement("SELECT r.reservation_id, r.reservation_ref, r.guest_id, r.room_id, r.user_id,        r.checkin_date, r.checkout_date, r.num_nights, r.status, r.special_requests,        r.created_at, r.updated_at,        g.guest_name, g.address, g.contact_number, g.email,        rm.room_number, rm.floor_number, rm.capacity,        rc.category_id, rc.category_name, rc.price_per_night, rc.description FROM   reservations r JOIN   guests        g  ON g.guest_id   = r.guest_id JOIN   rooms         rm ON rm.room_id   = r.room_id JOIN   room_categories rc ON rc.category_id = rm.category_id WHERE g.guest_name LIKE ? ORDER BY r.created_at DESC");

            try {
               ps.setString(1, "%" + name + "%");
               Throwable var6 = null;
               Object var7 = null;

               try {
                  ResultSet rs = ps.executeQuery();

                  try {
                     while(rs.next()) {
                        list.add(this.mapRow(rs));
                     }
                  } finally {
                     if (rs != null) {
                        rs.close();
                     }

                  }
               } catch (Throwable var30) {
                  if (var6 == null) {
                     var6 = var30;
                  } else if (var6 != var30) {
                     var6.addSuppressed(var30);
                  }

                  throw var6;
               }
            } finally {
               if (ps != null) {
                  ps.close();
               }

            }
         } catch (Throwable var32) {
            if (var3 == null) {
               var3 = var32;
            } else if (var3 != var32) {
               var3.addSuppressed(var32);
            }

            throw var3;
         }
      } catch (SQLException e) {
         LOGGER.log(Level.SEVERE, "Error finding reservations by guest name", e);
      }

      return list;
   }

   public boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut) {
      try {
         Throwable var4 = null;
         Object var5 = null;

         try {
            PreparedStatement ps = this.getConnection().prepareStatement("SELECT COUNT(*) FROM reservations WHERE  room_id = ? AND status NOT IN ('CANCELLED') AND    checkin_date < ? AND checkout_date > ?");

            ResultSet var10000;
            try {
               ps.setInt(1, roomId);
               ps.setDate(2, Date.valueOf(checkOut));
               ps.setDate(3, Date.valueOf(checkIn));
               Throwable var7 = null;
               Object var8 = null;

               try {
                  ResultSet rs;
                  var10000 = rs = ps.executeQuery();

                  try {
                     var10000 = rs.next() && rs.getInt(1) == 0;
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

            return (boolean)var10000;
         } catch (Throwable var33) {
            if (var4 == null) {
               var4 = var33;
            } else if (var4 != var33) {
               var4.addSuppressed(var33);
            }

            throw var4;
         }
      } catch (SQLException e) {
         LOGGER.log(Level.SEVERE, "Error checking room availability", e);
         return false;
      }
   }

   public boolean isRoomAvailableExcluding(int roomId, LocalDate checkIn, LocalDate checkOut, int excludeId) {
      try {
         Throwable var5 = null;
         Object var6 = null;

         try {
            PreparedStatement ps = this.getConnection().prepareStatement("SELECT COUNT(*) FROM reservations WHERE  room_id = ? AND reservation_id != ? AND status NOT IN ('CANCELLED') AND    checkin_date < ? AND checkout_date > ?");

            ResultSet var10000;
            try {
               ps.setInt(1, roomId);
               ps.setInt(2, excludeId);
               ps.setDate(3, Date.valueOf(checkOut));
               ps.setDate(4, Date.valueOf(checkIn));
               Throwable var8 = null;
               Object var9 = null;

               try {
                  ResultSet rs;
                  var10000 = rs = ps.executeQuery();

                  try {
                     var10000 = rs.next() && rs.getInt(1) == 0;
                  } finally {
                     if (rs != null) {
                        rs.close();
                     }

                  }
               } catch (Throwable var32) {
                  if (var8 == null) {
                     var8 = var32;
                  } else if (var8 != var32) {
                     var8.addSuppressed(var32);
                  }

                  throw var8;
               }
            } finally {
               if (ps != null) {
                  ps.close();
               }

            }

            return (boolean)var10000;
         } catch (Throwable var34) {
            if (var5 == null) {
               var5 = var34;
            } else if (var5 != var34) {
               var5.addSuppressed(var34);
            }

            throw var5;
         }
      } catch (SQLException e) {
         LOGGER.log(Level.SEVERE, "Error checking room availability (excluding)", e);
         return false;
      }
   }

   public boolean update(Reservation r) {
      try {
         Throwable var2 = null;
         Object var3 = null;

         try {
            PreparedStatement ps = this.getConnection().prepareStatement("UPDATE reservations SET room_id=?, checkin_date=?, checkout_date=?, num_nights=?, status=?, special_requests=?, updated_at=NOW() WHERE  reservation_id=?");

            boolean var10000;
            try {
               ps.setInt(1, r.getRoomId());
               ps.setDate(2, Date.valueOf(r.getCheckinDate()));
               ps.setDate(3, Date.valueOf(r.getCheckoutDate()));
               ps.setInt(4, r.getNumNights());
               ps.setString(5, r.getStatus().name());
               ps.setString(6, r.getSpecialRequests());
               ps.setInt(7, r.getReservationId());
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
         LOGGER.log(Level.SEVERE, "Error updating reservation", e);
         return false;
      }
   }

   public boolean cancel(int id) {
      try {
         Throwable var2 = null;
         Object var3 = null;

         try {
            PreparedStatement ps = this.getConnection().prepareStatement("UPDATE reservations SET status='CANCELLED', updated_at=NOW() WHERE reservation_id=?");

            boolean var10000;
            try {
               ps.setInt(1, id);
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
         LOGGER.log(Level.SEVERE, "Error cancelling reservation", e);
         return false;
      }
   }

   public int getNextSequence() {
      try {
         Throwable var1 = null;
         Object var2 = null;

         try {
            PreparedStatement ps = this.getConnection().prepareStatement("SELECT COALESCE(MAX(reservation_id), 0) + 1 FROM reservations");

            int var10000;
            label322: {
               try {
                  ResultSet rs = ps.executeQuery();

                  try {
                     if (rs.next()) {
                        var10000 = rs.getInt(1);
                        break label322;
                     }
                  } finally {
                     if (rs != null) {
                        rs.close();
                     }

                  }
               } catch (Throwable var18) {
                  if (var1 == null) {
                     var1 = var18;
                  } else if (var1 != var18) {
                     var1.addSuppressed(var18);
                  }

                  if (ps != null) {
                     ps.close();
                  }

                  throw var1;
               }

               if (ps != null) {
                  ps.close();
               }

               return 1;
            }

            if (ps != null) {
               ps.close();
            }

            return var10000;
         } catch (Throwable var19) {
            if (var1 == null) {
               var1 = var19;
            } else if (var1 != var19) {
               var1.addSuppressed(var19);
            }

            throw var1;
         }
      } catch (SQLException e) {
         LOGGER.log(Level.SEVERE, "Error getting next sequence", e);
         return 1;
      }
   }

   private int insertGuest(Connection conn, Guest guest) throws SQLException {
      Throwable var3 = null;
      Object var4 = null;

      try {
         PreparedStatement ps = conn.prepareStatement("INSERT INTO guests (guest_name, address, contact_number, email) VALUES (?, ?, ?, ?)", 1);

         ResultSet var10000;
         try {
            ps.setString(1, guest.getGuestName());
            ps.setString(2, guest.getAddress());
            ps.setString(3, guest.getContactNumber());
            ps.setString(4, guest.getEmail());
            ps.executeUpdate();
            Throwable var6 = null;
            Object var7 = null;

            try {
               ResultSet keys;
               var10000 = keys = ps.getGeneratedKeys();

               try {
                  var10000 = keys.next();
                  if (var10000 == false) {
                     throw new SQLException("Failed to insert guest record.");
                  }

                  var10000 = keys.getInt(1);
               } finally {
                  if (keys != null) {
                     keys.close();
                  }

               }
            } catch (Throwable var26) {
               if (var6 == null) {
                  var6 = var26;
               } else if (var6 != var26) {
                  var6.addSuppressed(var26);
               }

               throw var6;
            }
         } finally {
            if (ps != null) {
               ps.close();
            }

         }

         return (int)var10000;
      } catch (Throwable var28) {
         if (var3 == null) {
            var3 = var28;
         } else if (var3 != var28) {
            var3.addSuppressed(var28);
         }

         throw var3;
      }
   }

   private Optional<Reservation> querySingle(PreparedStatement ps) throws SQLException {
      Throwable var2 = null;
      Object var3 = null;

      try {
         ResultSet rs = ps.executeQuery();

         Optional var10000;
         try {
            if (!rs.next()) {
               return Optional.empty();
            }

            var10000 = Optional.of(this.mapRow(rs));
         } finally {
            if (rs != null) {
               rs.close();
            }

         }

         return var10000;
      } catch (Throwable var10) {
         if (var2 == null) {
            var2 = var10;
         } else if (var2 != var10) {
            var2.addSuppressed(var10);
         }

         throw var2;
      }
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
      if (createdAt != null) {
         r.setCreatedAt(createdAt.toLocalDateTime());
      }

      Timestamp updatedAt = rs.getTimestamp("updated_at");
      if (updatedAt != null) {
         r.setUpdatedAt(updatedAt.toLocalDateTime());
      }

      return r;
   }

   private void safeRollback(Connection conn) {
      try {
         if (conn != null) {
            conn.rollback();
         }
      } catch (SQLException var3) {
      }

   }

   private void safeSetAutoCommit(Connection conn) {
      try {
         if (conn != null) {
            conn.setAutoCommit(true);
         }
      } catch (SQLException var3) {
      }

   }
}
