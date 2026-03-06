// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.model;

import java.time.LocalDateTime;

public class User {
   private int userId;
   private String username;
   private String passwordHash;
   private String fullName;
   private String email;
   private int roleId;
   private String roleName;
   private boolean active;
   private LocalDateTime createdAt;

   public User() {
   }

   public User(int userId, String username, String fullName, String email, int roleId, String roleName, boolean active) {
      this.userId = userId;
      this.username = username;
      this.fullName = fullName;
      this.email = email;
      this.roleId = roleId;
      this.roleName = roleName;
      this.active = active;
   }

   public int getUserId() {
      return this.userId;
   }

   public void setUserId(int v) {
      this.userId = v;
   }

   public String getUsername() {
      return this.username;
   }

   public void setUsername(String v) {
      this.username = v;
   }

   public String getPasswordHash() {
      return this.passwordHash;
   }

   public void setPasswordHash(String v) {
      this.passwordHash = v;
   }

   public String getFullName() {
      return this.fullName;
   }

   public void setFullName(String v) {
      this.fullName = v;
   }

   public String getEmail() {
      return this.email;
   }

   public void setEmail(String v) {
      this.email = v;
   }

   public int getRoleId() {
      return this.roleId;
   }

   public void setRoleId(int v) {
      this.roleId = v;
   }

   public String getRoleName() {
      return this.roleName;
   }

   public void setRoleName(String v) {
      this.roleName = v;
   }

   public boolean isActive() {
      return this.active;
   }

   public void setActive(boolean v) {
      this.active = v;
   }

   public LocalDateTime getCreatedAt() {
      return this.createdAt;
   }

   public void setCreatedAt(LocalDateTime v) {
      this.createdAt = v;
   }

   public boolean isAdmin() {
      return "ADMIN".equalsIgnoreCase(this.roleName);
   }

   public String toString() {
      return "User{userId=" + this.userId + ", username='" + this.username + "', role='" + this.roleName + "'}";
   }
}
