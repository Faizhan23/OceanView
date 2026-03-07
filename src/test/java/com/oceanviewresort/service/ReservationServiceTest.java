package com.oceanviewresort.service;

import com.oceanviewresort.dao.ReservationDAO;
import com.oceanviewresort.dao.RoomDAO;
import com.oceanviewresort.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReservationService.
 * Uses Mockito to mock DAO dependencies – no database required.
 * Follows TDD-friendly structure with clear Arrange / Act / Assert sections.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationService Tests")
class ReservationServiceTest {

    @Mock private ReservationDAO reservationDAO;
    @Mock private RoomDAO        roomDAO;

    private ReservationService service;

    // ── Shared test fixtures ───────────────────────────────────────────────────
    private Room      testRoom;
    private String    validCheckIn;
    private String    validCheckOut;

    @BeforeEach
    void setUp() {
        service = new ReservationService(reservationDAO, roomDAO);

        RoomCategory cat = new RoomCategory(1, "Deluxe", 180.00, "Test room");
        testRoom = new Room(5, "201", cat, 2, 2, true);

        validCheckIn  = LocalDate.now().plusDays(1).toString();
        validCheckOut = LocalDate.now().plusDays(4).toString();
    }

    // ── createReservation – Happy Path ─────────────────────────────────────────
    @Test
    @DisplayName("createReservation: should save reservation and return generated ID")
    void createReservation_validInputs_returnsSavedId() {
        // Arrange
        when(roomDAO.findById(5)).thenReturn(Optional.of(testRoom));
        when(reservationDAO.isRoomAvailable(eq(5), any(), any())).thenReturn(true);
        when(reservationDAO.getNextSequence()).thenReturn(42);
        when(reservationDAO.save(any(Reservation.class))).thenReturn(99);

        // Act
        int id = service.createReservation(
                "John Smith", "123 Main St", "+94771234567", "john@test.com",
                5, validCheckIn, validCheckOut, "No special requests", 1);

        // Assert
        assertEquals(99, id);
        verify(reservationDAO, times(1)).save(any(Reservation.class));
    }

    // ── createReservation – Invalid Guest Name ─────────────────────────────────
    @Test
    @DisplayName("createReservation: should throw for invalid guest name")
    void createReservation_invalidGuestName_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createReservation(
                        "J0hn123!!!", "Address", "+94771234567", null,
                        5, validCheckIn, validCheckOut, null, 1),
                "Expected IllegalArgumentException for invalid name");
    }

    // ── createReservation – Invalid Date Range ─────────────────────────────────
    @Test
    @DisplayName("createReservation: should throw for past check-in date")
    void createReservation_pastCheckinDate_throwsIllegalArgument() {
        String pastDate = LocalDate.now().minusDays(1).toString();
        assertThrows(IllegalArgumentException.class, () ->
                service.createReservation(
                        "Jane Doe", "Address", "+94771234567", null,
                        5, pastDate, validCheckOut, null, 1));
    }

    @Test
    @DisplayName("createReservation: should throw when checkout is before checkin")
    void createReservation_checkoutBeforeCheckin_throwsIllegalArgument() {
        String future = LocalDate.now().plusDays(5).toString();
        assertThrows(IllegalArgumentException.class, () ->
                service.createReservation(
                        "Jane Doe", "Address", "+94771234567", null,
                        5, future, validCheckIn, null, 1));
    }

    // ── createReservation – Double Booking ────────────────────────────────────
    @Test
    @DisplayName("createReservation: should throw when room is not available")
    void createReservation_roomNotAvailable_throwsRuntimeException() {
        when(roomDAO.findById(5)).thenReturn(Optional.of(testRoom));
        when(reservationDAO.isRoomAvailable(eq(5), any(), any())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.createReservation(
                        "Jane Doe", "Address", "+94771234567", null,
                        5, validCheckIn, validCheckOut, null, 1));

        assertTrue(ex.getMessage().contains("DOUBLE_BOOKING"));
    }

    // ── createReservation – Room Not Found ────────────────────────────────────
    @Test
    @DisplayName("createReservation: should throw when room does not exist")
    void createReservation_roomNotFound_throwsIllegalArgument() {
        when(roomDAO.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                service.createReservation(
                        "John Smith", "123 St", "+94771234567", null,
                        999, validCheckIn, validCheckOut, null, 1));
    }

    // ── createReservation – Invalid Phone ─────────────────────────────────────
    @Test
    @DisplayName("createReservation: should throw for invalid phone number")
    void createReservation_invalidPhone_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createReservation(
                        "John Smith", "Address", "not-a-phone", null,
                        5, validCheckIn, validCheckOut, null, 1));
    }

    // ── getReservationById ─────────────────────────────────────────────────────
    @Test
    @DisplayName("getReservationById: should return reservation when found")
    void getReservationById_existingId_returnsReservation() {
        Reservation res = new Reservation();
        res.setReservationId(10);
        when(reservationDAO.findById(10)).thenReturn(Optional.of(res));

        Optional<Reservation> result = service.getReservationById(10);

        assertTrue(result.isPresent());
        assertEquals(10, result.get().getReservationId());
    }

    @Test
    @DisplayName("getReservationById: should return empty when not found")
    void getReservationById_nonExistingId_returnsEmpty() {
        when(reservationDAO.findById(999)).thenReturn(Optional.empty());

        assertTrue(service.getReservationById(999).isEmpty());
    }

    // ── getAllReservations ─────────────────────────────────────────────────────
    @Test
    @DisplayName("getAllReservations: should delegate to DAO")
    void getAllReservations_delegatesToDAO() {
        when(reservationDAO.findAll()).thenReturn(List.of(new Reservation(), new Reservation()));

        List<Reservation> list = service.getAllReservations();

        assertEquals(2, list.size());
        verify(reservationDAO, times(1)).findAll();
    }

    // ── cancelReservation ─────────────────────────────────────────────────────
    @Test
    @DisplayName("cancelReservation: should cancel existing active reservation")
    void cancelReservation_validId_returnsTrue() {
        Reservation res = new Reservation();
        res.setReservationId(7);
        res.setStatus(Reservation.Status.CONFIRMED);
        when(reservationDAO.findById(7)).thenReturn(Optional.of(res));
        when(reservationDAO.cancel(7)).thenReturn(true);

        assertTrue(service.cancelReservation(7));
    }

    @Test
    @DisplayName("cancelReservation: should throw when already cancelled")
    void cancelReservation_alreadyCancelled_throwsIllegalState() {
        Reservation res = new Reservation();
        res.setReservationId(7);
        res.setStatus(Reservation.Status.CANCELLED);
        when(reservationDAO.findById(7)).thenReturn(Optional.of(res));

        assertThrows(IllegalStateException.class, () -> service.cancelReservation(7));
    }

    // ── Night Calculation ─────────────────────────────────────────────────────
    @Test
    @DisplayName("Reservation: nights should be calculated automatically from dates")
    void reservation_nightsCalculatedFromDates() {
        Reservation r = new Reservation();
        r.setCheckinDate(LocalDate.now().plusDays(1));
        r.setCheckoutDate(LocalDate.now().plusDays(4));

        assertEquals(3, r.getNumNights());
    }
}
