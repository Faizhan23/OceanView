// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.model;

import java.time.LocalDateTime;

public class Guest {
   private int guestId;
   private String guestName;
   private String address;
   private String contactNumber;
   private String email;
   private LocalDateTime createdAt;

   public Guest() {
   }

   public Guest(String guestName, String address, String contactNumber, String email) {
      this.guestName = guestName;
      this.address = address;
      this.contactNumber = contactNumber;
      this.email = email;
   }

   public int getGuestId() {
      return this.guestId;
   }

   public void setGuestId(int v) {
      this.guestId = v;
   }

   public String getGuestName() {
      return this.guestName;
   }

   public void setGuestName(String v) {
      this.guestName = v;
   }

   public String getAddress() {
      return this.address;
   }

   public void setAddress(String v) {
      this.address = v;
   }

   public String getContactNumber() {
      return this.contactNumber;
   }

   public void setContactNumber(String v) {
      this.contactNumber = v;
   }

   public String getEmail() {
      return this.email;
   }

   public void setEmail(String v) {
      this.email = v;
   }

   public LocalDateTime getCreatedAt() {
      return this.createdAt;
   }

   public void setCreatedAt(LocalDateTime v) {
      this.createdAt = v;
   }

   public String toString() {
      return "Guest{guestId=" + this.guestId + ", name='" + this.guestName + "'}";
   }
}
