package com.oceanviewresort.model;


public class Room {

    private int          roomId;
    private String       roomNumber;
    private int          categoryId;
    private RoomCategory category;       
    private int          floorNumber;
    private int          capacity;
    private boolean      active;

    public Room() { }

    public Room(int roomId, String roomNumber, RoomCategory category,
                int floorNumber, int capacity, boolean active) {
        this.roomId      = roomId;
        this.roomNumber  = roomNumber;
        this.category    = category;
        this.categoryId  = category != null ? category.getCategoryId() : 0;
        this.floorNumber = floorNumber;
        this.capacity    = capacity;
        this.active      = active;
    }

    public int          getRoomId()            { return roomId; }
    public void         setRoomId(int v)       { this.roomId = v; }

    public String       getRoomNumber()            { return roomNumber; }
    public void         setRoomNumber(String v)    { this.roomNumber = v; }

    public int          getCategoryId()        { return categoryId; }
    public void         setCategoryId(int v)   { this.categoryId = v; }

    public RoomCategory getCategory()              { return category; }
    public void         setCategory(RoomCategory v){ this.category = v; }

    public int          getFloorNumber()       { return floorNumber; }
    public void         setFloorNumber(int v)  { this.floorNumber = v; }

    public int          getCapacity()          { return capacity; }
    public void         setCapacity(int v)     { this.capacity = v; }

    public boolean      isActive()             { return active; }
    public void         setActive(boolean v)   { this.active = v; }

    
    public double getPricePerNight() {
        return category != null ? category.getPricePerNight() : 0.0;
    }

    
    public String getCategoryName() {
        return category != null ? category.getCategoryName() : "Unknown";
    }

    @Override
    public String toString() {
        return "Room{roomId=" + roomId + ", roomNumber='" + roomNumber +
               "', category='" + getCategoryName() + "', floor=" + floorNumber + "}";
    }
}
