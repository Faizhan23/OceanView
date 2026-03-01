package com.oceanviewresort.model;

import java.time.LocalDateTime;

/**
 * Domain model representing a system user (staff or admin).
 */
public class User {

    private int           userId;
    private String        username;
    private String        passwordHash;
    private String        fullName;
    private String        email;
    private int           roleId;
    private String        roleName;
    private boolean       active;
    private LocalDateTime createdAt;

    public User() { }

    public User(int userId, String username, String fullName, String email,
                int roleId, String roleName, boolean active) {
        this.userId   = userId;
        this.username = username;
        this.fullName = fullName;
        this.email    = email;
        this.roleId   = roleId;
        this.roleName = roleName;
        this.active   = active;
    }

    // ── Getters & Setters ──────────────────────────────────────
    public int           getUserId()       { return userId; }
    public void          setUserId(int v)  { this.userId = v; }

    public String        getUsername()          { return username; }
    public void          setUsername(String v)  { this.username = v; }

    public String        getPasswordHash()         { return passwordHash; }
    public void          setPasswordHash(String v) { this.passwordHash = v; }

    public String        getFullName()          { return fullName; }
    public void          setFullName(String v)  { this.fullName = v; }

    public String        getEmail()          { return email; }
    public void          setEmail(String v)  { this.email = v; }

    public int           getRoleId()       { return roleId; }
    public void          setRoleId(int v)  { this.roleId = v; }

    public String        getRoleName()          { return roleName; }
    public void          setRoleName(String v)  { this.roleName = v; }

    public boolean       isActive()         { return active; }
    public void          setActive(boolean v) { this.active = v; }

    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void          setCreatedAt(LocalDateTime v) { this.createdAt = v; }

    public boolean isAdmin() { return "ADMIN".equalsIgnoreCase(roleName); }

    @Override
    public String toString() {
        return "User{userId=" + userId + ", username='" + username + "', role='" + roleName + "'}";
    }
}
