package com.oceanviewresort.dao;

import com.oceanviewresort.model.Reservation;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface ReservationDAO {

    int  save(Reservation reservation);

    Optional<Reservation> findById(int reservationId);

    Optional<Reservation> findByRef(String reservationRef);

    List<Reservation> findAll();

    List<Reservation> findByGuestName(String name);

    boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut);

    boolean isRoomAvailableExcluding(int roomId, LocalDate checkIn, LocalDate checkOut, int excludeReservationId);

    boolean update(Reservation reservation);

    boolean cancel(int reservationId);

    int getNextSequence();
}
