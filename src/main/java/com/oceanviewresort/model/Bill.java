// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.model;

import java.time.LocalDateTime;

public class Bill {
   private int billId;
   private int reservationId;
   private Reservation reservation;
   private double roomCharge;
   private double taxAmount;
   private double discount;
   private double totalAmount;
   private double taxRate = (double)10.0F;
   private boolean paid;
   private LocalDateTime generatedAt;
   private LocalDateTime paidAt;

   public Bill() {
   }

   public int getBillId() {
      return this.billId;
   }

   public void setBillId(int v) {
      this.billId = v;
   }

   public int getReservationId() {
      return this.reservationId;
   }

   public void setReservationId(int v) {
      this.reservationId = v;
   }

   public Reservation getReservation() {
      return this.reservation;
   }

   public void setReservation(Reservation v) {
      this.reservation = v;
   }

   public double getRoomCharge() {
      return this.roomCharge;
   }

   public void setRoomCharge(double v) {
      this.roomCharge = v;
   }

   public double getTaxAmount() {
      return this.taxAmount;
   }

   public void setTaxAmount(double v) {
      this.taxAmount = v;
   }

   public double getDiscount() {
      return this.discount;
   }

   public void setDiscount(double v) {
      this.discount = v;
   }

   public double getTotalAmount() {
      return this.totalAmount;
   }

   public void setTotalAmount(double v) {
      this.totalAmount = v;
   }

   public double getTaxRate() {
      return this.taxRate;
   }

   public void setTaxRate(double v) {
      this.taxRate = v;
   }

   public boolean isPaid() {
      return this.paid;
   }

   public void setPaid(boolean v) {
      this.paid = v;
   }

   public LocalDateTime getGeneratedAt() {
      return this.generatedAt;
   }

   public void setGeneratedAt(LocalDateTime v) {
      this.generatedAt = v;
   }

   public LocalDateTime getPaidAt() {
      return this.paidAt;
   }

   public void setPaidAt(LocalDateTime v) {
      this.paidAt = v;
   }

   public double getSubtotal() {
      return this.roomCharge - this.discount;
   }

   public String toString() {
      return "Bill{billId=" + this.billId + ", total=" + this.totalAmount + ", paid=" + this.paid + "}";
   }
}
