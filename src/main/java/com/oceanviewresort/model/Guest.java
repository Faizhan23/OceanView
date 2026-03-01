package com.oceanviewresort.model;

import java.time.LocalDateTime;


public class Guest {

    private int           guestId;
    private String        guestName;
    private String        address;
    private String        contactNumber;
    private String        email;
    private LocalDateTime createdAt;

    public Guest() { }

    public Guest(String guestName, String address, String contactNumber, String email) {
        this.guestName     = guestName;
        this.address       = address;
        this.contactNumber = contactNumber;
        this.email         = email;
    }

    public int    getGuestId()          { return guestId; }
    public void   setGuestId(int v)     { this.guestId = v; }

    public String getGuestName()            { return guestName; }
    public void   setGuestName(String v)    { this.guestName = v; }

    public String getAddress()          { return address; }
    public void   setAddress(String v)  { this.address = v; }

    public String getContactNumber()           { return contactNumber; }
    public void   setContactNumber(String v)   { this.contactNumber = v; }

    public String getEmail()            { return email; }
    public void   setEmail(String v)    { this.email = v; }

    public LocalDateTime getCreatedAt()             { return createdAt; }
    public void          setCreatedAt(LocalDateTime v) { this.createdAt = v; }

    @Override
    public String toString() {
        return "Guest{guestId=" + guestId + ", name='" + guestName + "'}";
    }
}
