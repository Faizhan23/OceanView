// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.dao;

import com.oceanviewresort.model.Reservation;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationDAO {
   int save(Reservation var1);

   Optional<Reservation> findById(int var1);

   Optional<Reservation> findByRef(String var1);

   List<Reservation> findAll();

   List<Reservation> findByGuestName(String var1);

   boolean isRoomAvailable(int var1, LocalDate var2, LocalDate var3);

   boolean isRoomAvailableExcluding(int var1, LocalDate var2, LocalDate var3, int var4);

   boolean update(Reservation var1);

   boolean cancel(int var1);

   int getNextSequence();
}
