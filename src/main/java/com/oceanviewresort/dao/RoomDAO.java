package com.oceanviewresort.dao;

import com.oceanviewresort.model.Room;
import com.oceanviewresort.model.RoomCategory;

import java.util.List;
import java.util.Optional;

public interface RoomDAO {
    List<Room>         findAll();
    Optional<Room>     findById(int roomId);
    List<Room>         findAvailableRooms(String checkIn, String checkOut);
    List<RoomCategory> findAllCategories();
}
