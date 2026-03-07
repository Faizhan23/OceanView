// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.model;

public class Room {
   private int roomId;
   private String roomNumber;
   private int categoryId;
   private RoomCategory category;
   private int floorNumber;
   private int capacity;
   private boolean active;

   public Room() {
   }

   public Room(int roomId, String roomNumber, RoomCategory category, int floorNumber, int capacity, boolean active) {
      this.roomId = roomId;
      this.roomNumber = roomNumber;
      this.category = category;
      this.categoryId = category != null ? category.getCategoryId() : 0;
      this.floorNumber = floorNumber;
      this.capacity = capacity;
      this.active = active;
   }

   public int getRoomId() {
      return this.roomId;
   }

   public void setRoomId(int v) {
      this.roomId = v;
   }

   public String getRoomNumber() {
      return this.roomNumber;
   }

   public void setRoomNumber(String v) {
      this.roomNumber = v;
   }

   public int getCategoryId() {
      return this.categoryId;
   }

   public void setCategoryId(int v) {
      this.categoryId = v;
   }

   public RoomCategory getCategory() {
      return this.category;
   }

   public void setCategory(RoomCategory v) {
      this.category = v;
   }

   public int getFloorNumber() {
      return this.floorNumber;
   }

   public void setFloorNumber(int v) {
      this.floorNumber = v;
   }

   public int getCapacity() {
      return this.capacity;
   }

   public void setCapacity(int v) {
      this.capacity = v;
   }

   public boolean isActive() {
      return this.active;
   }

   public void setActive(boolean v) {
      this.active = v;
   }

   public double getPricePerNight() {
      return this.category != null ? this.category.getPricePerNight() : (double)0.0F;
   }

   public String getCategoryName() {
      return this.category != null ? this.category.getCategoryName() : "Unknown";
   }

   public String toString() {
      int var10000 = this.roomId;
      return "Room{roomId=" + var10000 + ", roomNumber='" + this.roomNumber + "', category='" + this.getCategoryName() + "', floor=" + this.floorNumber + "}";
   }
}
