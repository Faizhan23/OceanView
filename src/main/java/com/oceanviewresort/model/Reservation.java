// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Reservation {
   private int reservationId;
   private String reservationRef;
   private int guestId;
   private Guest guest;
   private int roomId;
   private Room room;
   private int userId;
   private LocalDate checkinDate;
   private LocalDate checkoutDate;
   private int numNights;
   private Status status;
   private String specialRequests;
   private LocalDateTime createdAt;
   private LocalDateTime updatedAt;

   public Reservation() {
      this.status = Reservation.Status.CONFIRMED;
   }

   public void calculateNights() {
      if (this.checkinDate != null && this.checkoutDate != null) {
         this.numNights = (int)ChronoUnit.DAYS.between(this.checkinDate, this.checkoutDate);
      }

   }

   public int getReservationId() {
      return this.reservationId;
   }

   public void setReservationId(int v) {
      this.reservationId = v;
   }

   public String getReservationRef() {
      return this.reservationRef;
   }

   public void setReservationRef(String v) {
      this.reservationRef = v;
   }

   public int getGuestId() {
      return this.guestId;
   }

   public void setGuestId(int v) {
      this.guestId = v;
   }

   public Guest getGuest() {
      return this.guest;
   }

   public void setGuest(Guest v) {
      this.guest = v;
      if (v != null) {
         this.guestId = v.getGuestId();
      }

   }

   public int getRoomId() {
      return this.roomId;
   }

   public void setRoomId(int v) {
      this.roomId = v;
   }

   public Room getRoom() {
      return this.room;
   }

   public void setRoom(Room v) {
      this.room = v;
      if (v != null) {
         this.roomId = v.getRoomId();
      }

   }

   public int getUserId() {
      return this.userId;
   }

   public void setUserId(int v) {
      this.userId = v;
   }

   public LocalDate getCheckinDate() {
      return this.checkinDate;
   }

   public void setCheckinDate(LocalDate v) {
      this.checkinDate = v;
      this.calculateNights();
   }

   public LocalDate getCheckoutDate() {
      return this.checkoutDate;
   }

   public void setCheckoutDate(LocalDate v) {
      this.checkoutDate = v;
      this.calculateNights();
   }

   public int getNumNights() {
      return this.numNights;
   }

   public void setNumNights(int v) {
      this.numNights = v;
   }

   public Status getStatus() {
      return this.status;
   }

   public void setStatus(Status v) {
      this.status = v;
   }

   public void setStatusFromString(String s) {
      try {
         this.status = Reservation.Status.valueOf(s);
      } catch (Exception var3) {
         this.status = Reservation.Status.CONFIRMED;
      }

   }

   public String getSpecialRequests() {
      return this.specialRequests;
   }

   public void setSpecialRequests(String v) {
      this.specialRequests = v;
   }

   public LocalDateTime getCreatedAt() {
      return this.createdAt;
   }

   public void setCreatedAt(LocalDateTime v) {
      this.createdAt = v;
   }

   public LocalDateTime getUpdatedAt() {
      return this.updatedAt;
   }

   public void setUpdatedAt(LocalDateTime v) {
      this.updatedAt = v;
   }

   public double estimatedRoomCharge() {
      return this.room == null ? (double)0.0F : (double)this.numNights * this.room.getPricePerNight();
   }

   public String toString() {
      String var10000 = this.reservationRef;
      return "Reservation{ref='" + var10000 + "', status=" + String.valueOf(this.status) + ", nights=" + this.numNights + "}";
   }

   public static enum Status {
      PENDING,
      CONFIRMED,
      CHECKED_IN,
      CHECKED_OUT,
      CANCELLED;

      private Status() {
      }
   }
}
