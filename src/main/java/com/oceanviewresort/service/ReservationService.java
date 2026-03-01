package com.oceanviewresort.service;

import com.oceanviewresort.dao.ReservationDAO;
import com.oceanviewresort.dao.ReservationDAOImpl;
import com.oceanviewresort.dao.RoomDAO;
import com.oceanviewresort.dao.RoomDAOImpl;
import com.oceanviewresort.model.Guest;
import com.oceanviewresort.model.Reservation;
import com.oceanviewresort.model.Room;
import com.oceanviewresort.util.ReservationRefGenerator;
import com.oceanviewresort.util.ValidationUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Service class containing all reservation business logic.
 * Orchestrates DAO calls; validates inputs; enforces business rules.
 * No SQL, no HTTP concepts here.
 */
public class ReservationService {

    private static final Logger LOGGER = Logger.getLogger(ReservationService.class.getName());

    private final ReservationDAO reservationDAO;
    private final RoomDAO        roomDAO;

    /** Production constructor */
    public ReservationService() {
        this(new ReservationDAOImpl(), new RoomDAOImpl());
    }

    /** Testable constructor – inject mock DAOs */
    public ReservationService(ReservationDAO reservationDAO, RoomDAO roomDAO) {
        this.reservationDAO = reservationDAO;
        this.roomDAO        = roomDAO;
    }

    /**
     * Creates a new reservation after validating all inputs and checking availability.
     *
     * @return the generated reservation ID
     * @throws IllegalArgumentException on validation failures
     * @throws RuntimeException         on double-booking (from DB trigger)
     */
    public int createReservation(String guestName, String address, String contactNumber,
                                 String email, int roomId, String checkIn, String checkOut,
                                 String specialRequests, int createdByUserId) {

        // ── Input Validation ───────────────────────────────────────────────────
        if (!ValidationUtil.isValidGuestName(guestName)) {
            throw new IllegalArgumentException("Invalid guest name. Use letters only (2–100 characters).");
        }
        if (!ValidationUtil.isNotBlank(address)) {
            throw new IllegalArgumentException("Address is required.");
        }
        if (!ValidationUtil.isValidPhone(contactNumber)) {
            throw new IllegalArgumentException("Invalid contact number format.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email address format.");
        }
        if (!ValidationUtil.isValidDateRange(checkIn, checkOut)) {
            throw new IllegalArgumentException(
                    "Invalid date range. Check-in must be today or later, check-out must be after check-in.");
        }

        LocalDate ciDate = LocalDate.parse(checkIn.trim());
        LocalDate coDate = LocalDate.parse(checkOut.trim());

        // ── Room Existence Check ───────────────────────────────────────────────
        Room room = roomDAO.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Selected room does not exist."));

        // ── Availability Check (application-level guard before DB trigger) ─────
        if (!reservationDAO.isRoomAvailable(roomId, ciDate, coDate)) {
            throw new RuntimeException(
                    "DOUBLE_BOOKING: Room " + room.getRoomNumber() +
                    " is already reserved for the selected dates. Please choose another room or dates.");
        }

        // ── Build Domain Objects ───────────────────────────────────────────────
        Guest guest = new Guest(
                guestName.trim(),
                address.trim(),
                contactNumber.trim(),
                email == null ? null : email.trim()
        );

        Reservation reservation = new Reservation();
        reservation.setGuest(guest);
        reservation.setRoom(room);
        reservation.setRoomId(roomId);
        reservation.setCheckinDate(ciDate);
        reservation.setCheckoutDate(coDate);
        reservation.calculateNights();
        reservation.setUserId(createdByUserId);
        reservation.setSpecialRequests(specialRequests);
        reservation.setStatus(Reservation.Status.CONFIRMED);

        // ── Generate Reference ─────────────────────────────────────────────────
        int seq = reservationDAO.getNextSequence();
        reservation.setReservationRef(ReservationRefGenerator.generate(seq));

        // ── Persist (DB trigger is second layer of double-booking prevention) ──
        int id = reservationDAO.save(reservation);
        LOGGER.info("Reservation created: " + reservation.getReservationRef() + " (id=" + id + ")");
        return id;
    }

    /**
     * Returns a single reservation by ID.
     */
    public Optional<Reservation> getReservationById(int id) {
        return reservationDAO.findById(id);
    }

    /**
     * Returns a single reservation by reference number.
     */
    public Optional<Reservation> getReservationByRef(String ref) {
        if (!ValidationUtil.isNotBlank(ref)) return Optional.empty();
        return reservationDAO.findByRef(ref.trim());
    }

    /**
     * Returns all reservations (admin view).
     */
    public List<Reservation> getAllReservations() {
        return reservationDAO.findAll();
    }

    /**
     * Search by guest name.
     */
    public List<Reservation> searchByGuestName(String name) {
        if (!ValidationUtil.isNotBlank(name)) return List.of();
        return reservationDAO.findByGuestName(name.trim());
    }

    /**
     * Returns available rooms for given dates.
     */
    public List<Room> getAvailableRooms(String checkIn, String checkOut) {
        if (!ValidationUtil.isValidDateRange(checkIn, checkOut)) {
            throw new IllegalArgumentException("Invalid date range.");
        }
        return roomDAO.findAvailableRooms(checkIn, checkOut);
    }

    /**
     * Cancels a reservation.
     */
    public boolean cancelReservation(int reservationId) {
        Optional<Reservation> opt = reservationDAO.findById(reservationId);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }
        Reservation r = opt.get();
        if (r.getStatus() == Reservation.Status.CANCELLED) {
            throw new IllegalStateException("Reservation is already cancelled.");
        }
        return reservationDAO.cancel(reservationId);
    }
}
