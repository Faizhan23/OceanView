// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.service;

import com.oceanviewresort.dao.ReservationDAO;
import com.oceanviewresort.dao.RoomDAO;
import com.oceanviewresort.model.Reservation;
import com.oceanviewresort.model.Room;
import com.oceanviewresort.model.RoomCategory;
import com.oceanviewresort.model.Reservation.Status;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
@DisplayName("ReservationService Tests")
class ReservationServiceTest {
   @Mock
   private ReservationDAO reservationDAO;
   @Mock
   private RoomDAO roomDAO;
   private ReservationService service;
   private Room testRoom;
   private String validCheckIn;
   private String validCheckOut;

   ReservationServiceTest() {
   }

   @BeforeEach
   void setUp() {
      this.service = new ReservationService(this.reservationDAO, this.roomDAO);
      RoomCategory cat = new RoomCategory(1, "Deluxe", (double)180.0F, "Test room");
      this.testRoom = new Room(5, "201", cat, 2, 2, true);
      this.validCheckIn = LocalDate.now().plusDays(1L).toString();
      this.validCheckOut = LocalDate.now().plusDays(4L).toString();
   }

   @Test
   @DisplayName("createReservation: should save reservation and return generated ID")
   void createReservation_validInputs_returnsSavedId() {
      Mockito.when(this.roomDAO.findById(5)).thenReturn(Optional.of(this.testRoom));
      Mockito.when(this.reservationDAO.isRoomAvailable(Mockito.eq(5), (LocalDate)Mockito.any(), (LocalDate)Mockito.any())).thenReturn(true);
      Mockito.when(this.reservationDAO.getNextSequence()).thenReturn(42);
      Mockito.when(this.reservationDAO.save((Reservation)Mockito.any(Reservation.class))).thenReturn(99);
      int id = this.service.createReservation("John Smith", "123 Main St", "+94771234567", "john@test.com", 5, this.validCheckIn, this.validCheckOut, "No special requests", 1);
      Assertions.assertEquals(99, id);
      ((ReservationDAO)Mockito.verify(this.reservationDAO, Mockito.times(1))).save((Reservation)Mockito.any(Reservation.class));
   }

   @Test
   @DisplayName("createReservation: should throw for invalid guest name")
   void createReservation_invalidGuestName_throwsIllegalArgument() {
      Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.createReservation("J0hn123!!!", "Address", "+94771234567", (String)null, 5, this.validCheckIn, this.validCheckOut, (String)null, 1), "Expected IllegalArgumentException for invalid name");
   }

   @Test
   @DisplayName("createReservation: should throw for past check-in date")
   void createReservation_pastCheckinDate_throwsIllegalArgument() {
      String pastDate = LocalDate.now().minusDays(1L).toString();
      Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.createReservation("Jane Doe", "Address", "+94771234567", (String)null, 5, pastDate, this.validCheckOut, (String)null, 1));
   }

   @Test
   @DisplayName("createReservation: should throw when checkout is before checkin")
   void createReservation_checkoutBeforeCheckin_throwsIllegalArgument() {
      String future = LocalDate.now().plusDays(5L).toString();
      Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.createReservation("Jane Doe", "Address", "+94771234567", (String)null, 5, future, this.validCheckIn, (String)null, 1));
   }

   @Test
   @DisplayName("createReservation: should throw when room is not available")
   void createReservation_roomNotAvailable_throwsRuntimeException() {
      Mockito.when(this.roomDAO.findById(5)).thenReturn(Optional.of(this.testRoom));
      Mockito.when(this.reservationDAO.isRoomAvailable(Mockito.eq(5), (LocalDate)Mockito.any(), (LocalDate)Mockito.any())).thenReturn(false);
      RuntimeException ex = (RuntimeException)Assertions.assertThrows(RuntimeException.class, () -> this.service.createReservation("Jane Doe", "Address", "+94771234567", (String)null, 5, this.validCheckIn, this.validCheckOut, (String)null, 1));
      Assertions.assertTrue(ex.getMessage().contains("DOUBLE_BOOKING"));
   }

   @Test
   @DisplayName("createReservation: should throw when room does not exist")
   void createReservation_roomNotFound_throwsIllegalArgument() {
      Mockito.when(this.roomDAO.findById(999)).thenReturn(Optional.empty());
      Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.createReservation("John Smith", "123 St", "+94771234567", (String)null, 999, this.validCheckIn, this.validCheckOut, (String)null, 1));
   }

   @Test
   @DisplayName("createReservation: should throw for invalid phone number")
   void createReservation_invalidPhone_throwsIllegalArgument() {
      Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.createReservation("John Smith", "Address", "not-a-phone", (String)null, 5, this.validCheckIn, this.validCheckOut, (String)null, 1));
   }

   @Test
   @DisplayName("getReservationById: should return reservation when found")
   void getReservationById_existingId_returnsReservation() {
      Reservation res = new Reservation();
      res.setReservationId(10);
      Mockito.when(this.reservationDAO.findById(10)).thenReturn(Optional.of(res));
      Optional<Reservation> result = this.service.getReservationById(10);
      Assertions.assertTrue(result.isPresent());
      Assertions.assertEquals(10, ((Reservation)result.get()).getReservationId());
   }

   @Test
   @DisplayName("getReservationById: should return empty when not found")
   void getReservationById_nonExistingId_returnsEmpty() {
      Mockito.when(this.reservationDAO.findById(999)).thenReturn(Optional.empty());
      Assertions.assertTrue(this.service.getReservationById(999).isEmpty());
   }

   @Test
   @DisplayName("getAllReservations: should delegate to DAO")
   void getAllReservations_delegatesToDAO() {
      Mockito.when(this.reservationDAO.findAll()).thenReturn(List.of(new Reservation(), new Reservation()));
      List<Reservation> list = this.service.getAllReservations();
      Assertions.assertEquals(2, list.size());
      ((ReservationDAO)Mockito.verify(this.reservationDAO, Mockito.times(1))).findAll();
   }

   @Test
   @DisplayName("cancelReservation: should cancel existing active reservation")
   void cancelReservation_validId_returnsTrue() {
      Reservation res = new Reservation();
      res.setReservationId(7);
      res.setStatus(Status.CONFIRMED);
      Mockito.when(this.reservationDAO.findById(7)).thenReturn(Optional.of(res));
      Mockito.when(this.reservationDAO.cancel(7)).thenReturn(true);
      Assertions.assertTrue(this.service.cancelReservation(7));
   }

   @Test
   @DisplayName("cancelReservation: should throw when already cancelled")
   void cancelReservation_alreadyCancelled_throwsIllegalState() {
      Reservation res = new Reservation();
      res.setReservationId(7);
      res.setStatus(Status.CANCELLED);
      Mockito.when(this.reservationDAO.findById(7)).thenReturn(Optional.of(res));
      Assertions.assertThrows(IllegalStateException.class, () -> this.service.cancelReservation(7));
   }

   @Test
   @DisplayName("Reservation: nights should be calculated automatically from dates")
   void reservation_nightsCalculatedFromDates() {
      Reservation r = new Reservation();
      r.setCheckinDate(LocalDate.now().plusDays(1L));
      r.setCheckoutDate(LocalDate.now().plusDays(4L));
      Assertions.assertEquals(3, r.getNumNights());
   }
}
