// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.dao;

import com.oceanviewresort.model.Room;
import com.oceanviewresort.model.RoomCategory;
import java.util.List;
import java.util.Optional;

public interface RoomDAO {
   List<Room> findAll();

   Optional<Room> findById(int var1);

   List<Room> findAvailableRooms(String var1, String var2);

   List<RoomCategory> findAllCategories();
}
