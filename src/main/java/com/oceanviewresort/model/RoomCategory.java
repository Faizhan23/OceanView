// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.model;

public class RoomCategory {
   private int categoryId;
   private String categoryName;
   private double pricePerNight;
   private String description;

   public RoomCategory() {
   }

   public RoomCategory(int categoryId, String categoryName, double pricePerNight, String description) {
      this.categoryId = categoryId;
      this.categoryName = categoryName;
      this.pricePerNight = pricePerNight;
      this.description = description;
   }

   public int getCategoryId() {
      return this.categoryId;
   }

   public void setCategoryId(int v) {
      this.categoryId = v;
   }

   public String getCategoryName() {
      return this.categoryName;
   }

   public void setCategoryName(String v) {
      this.categoryName = v;
   }

   public double getPricePerNight() {
      return this.pricePerNight;
   }

   public void setPricePerNight(double v) {
      this.pricePerNight = v;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String v) {
      this.description = v;
   }

   public String toString() {
      return this.categoryName + " ($" + this.pricePerNight + "/night)";
   }
}
