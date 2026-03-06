// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.service;

import com.oceanviewresort.dao.ReservationDAO;
import com.oceanviewresort.dao.ReservationDAOImpl;
import com.oceanviewresort.dao.RoomDAO;
import com.oceanviewresort.dao.RoomDAOImpl;
import com.oceanviewresort.model.Guest;
import com.oceanviewresort.model.Reservation;
import com.oceanviewresort.model.Room;
import com.oceanviewresort.model.Reservation.Status;
import com.oceanviewresort.util.ReservationRefGenerator;
import com.oceanviewresort.util.ValidationUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class ReservationService {
   private static final Logger LOGGER = Logger.getLogger(ReservationService.class.getName());
   private final ReservationDAO reservationDAO;
   private final RoomDAO roomDAO;

   public ReservationService() {
      this(new ReservationDAOImpl(), new RoomDAOImpl());
   }

   public ReservationService(ReservationDAO reservationDAO, RoomDAO roomDAO) {
      this.reservationDAO = reservationDAO;
      this.roomDAO = roomDAO;
   }

   public int createReservation(String guestName, String address, String contactNumber, String email, int roomId, String checkIn, String checkOut, String specialRequests, int createdByUserId) {
      if (!ValidationUtil.isValidGuestName(guestName)) {
         throw new IllegalArgumentException("Invalid guest name. Use letters only (2–100 characters).");
      } else if (!ValidationUtil.isNotBlank(address)) {
         throw new IllegalArgumentException("Address is required.");
      } else if (!ValidationUtil.isValidPhone(contactNumber)) {
         throw new IllegalArgumentException("Invalid contact number format.");
      } else if (!ValidationUtil.isValidEmail(email)) {
         throw new IllegalArgumentException("Invalid email address format.");
      } else if (!ValidationUtil.isValidDateRange(checkIn, checkOut)) {
         throw new IllegalArgumentException("Invalid date range. Check-in must be today or later, check-out must be after check-in.");
      } else {
         LocalDate ciDate = LocalDate.parse(checkIn.trim());
         LocalDate coDate = LocalDate.parse(checkOut.trim());
         Room room = (Room)this.roomDAO.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Selected room does not exist."));
         if (!this.reservationDAO.isRoomAvailable(roomId, ciDate, coDate)) {
            throw new RuntimeException("DOUBLE_BOOKING: Room " + room.getRoomNumber() + " is already reserved for the selected dates. Please choose another room or dates.");
         } else {
            Guest guest = new Guest(guestName.trim(), address.trim(), contactNumber.trim(), email == null ? null : email.trim());
            Reservation reservation = new Reservation();
            reservation.setGuest(guest);
            reservation.setRoom(room);
            reservation.setRoomId(roomId);
            reservation.setCheckinDate(ciDate);
            reservation.setCheckoutDate(coDate);
            reservation.calculateNights();
            reservation.setUserId(createdByUserId);
            reservation.setSpecialRequests(specialRequests);
            reservation.setStatus(Status.CONFIRMED);
            int seq = this.reservationDAO.getNextSequence();
            reservation.setReservationRef(ReservationRefGenerator.generate(seq));
            int id = this.reservationDAO.save(reservation);
            Logger var10000 = LOGGER;
            String var10001 = reservation.getReservationRef();
            var10000.info("Reservation created: " + var10001 + " (id=" + id + ")");
            return id;
         }
      }
   }

   public Optional<Reservation> getReservationById(int id) {
      return this.reservationDAO.findById(id);
   }

   public Optional<Reservation> getReservationByRef(String ref) {
      return !ValidationUtil.isNotBlank(ref) ? Optional.empty() : this.reservationDAO.findByRef(ref.trim());
   }

   public List<Reservation> getAllReservations() {
      return this.reservationDAO.findAll();
   }

   public List<Reservation> searchByGuestName(String name) {
      return !ValidationUtil.isNotBlank(name) ? List.of() : this.reservationDAO.findByGuestName(name.trim());
   }

   public List<Room> getAvailableRooms(String checkIn, String checkOut) {
      if (!ValidationUtil.isValidDateRange(checkIn, checkOut)) {
         throw new IllegalArgumentException("Invalid date range.");
      } else {
         return this.roomDAO.findAvailableRooms(checkIn, checkOut);
      }
   }

   public boolean cancelReservation(int reservationId) {
      Optional<Reservation> opt = this.reservationDAO.findById(reservationId);
      if (opt.isEmpty()) {
         throw new IllegalArgumentException("Reservation not found: " + reservationId);
      } else {
         Reservation r = (Reservation)opt.get();
         if (r.getStatus() == Status.CANCELLED) {
            throw new IllegalStateException("Reservation is already cancelled.");
         } else {
            return this.reservationDAO.cancel(reservationId);
         }
      }
   }
}
