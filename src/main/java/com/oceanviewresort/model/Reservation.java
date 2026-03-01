package com.oceanviewresort.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


public class Reservation {

    public enum Status {
        PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED
    }

    private int           reservationId;
    private String        reservationRef;
    private int           guestId;
    private Guest         guest;
    private int           roomId;
    private Room          room;
    private int           userId;          
    private LocalDate     checkinDate;
    private LocalDate     checkoutDate;
    private int           numNights;
    private Status        status;
    private String        specialRequests;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Reservation() {
        this.status = Status.CONFIRMED;
    }

    public void calculateNights() {
        if (checkinDate != null && checkoutDate != null) {
            this.numNights = (int) ChronoUnit.DAYS.between(checkinDate, checkoutDate);
        }
    }

    public int    getReservationId()        { return reservationId; }
    public void   setReservationId(int v)   { this.reservationId = v; }

    public String getReservationRef()           { return reservationRef; }
    public void   setReservationRef(String v)   { this.reservationRef = v; }

    public int    getGuestId()          { return guestId; }
    public void   setGuestId(int v)     { this.guestId = v; }

    public Guest  getGuest()            { return guest; }
    public void   setGuest(Guest v)     { this.guest = v; if (v != null) guestId = v.getGuestId(); }

    public int    getRoomId()           { return roomId; }
    public void   setRoomId(int v)      { this.roomId = v; }

    public Room   getRoom()             { return room; }
    public void   setRoom(Room v)       { this.room = v; if (v != null) roomId = v.getRoomId(); }

    public int    getUserId()           { return userId; }
    public void   setUserId(int v)      { this.userId = v; }

    public LocalDate getCheckinDate()          { return checkinDate; }
    public void      setCheckinDate(LocalDate v) { this.checkinDate = v; calculateNights(); }

    public LocalDate getCheckoutDate()           { return checkoutDate; }
    public void      setCheckoutDate(LocalDate v){ this.checkoutDate = v; calculateNights(); }

    public int    getNumNights()        { return numNights; }
    public void   setNumNights(int v)   { this.numNights = v; }

    public Status getStatus()           { return status; }
    public void   setStatus(Status v)   { this.status = v; }

    public void   setStatusFromString(String s) {
        try { this.status = Status.valueOf(s); } catch (Exception e) { this.status = Status.CONFIRMED; }
    }

    public String getSpecialRequests()          { return specialRequests; }
    public void   setSpecialRequests(String v)  { this.specialRequests = v; }

    public LocalDateTime getCreatedAt()             { return createdAt; }
    public void          setCreatedAt(LocalDateTime v) { this.createdAt = v; }

    public LocalDateTime getUpdatedAt()             { return updatedAt; }
    public void          setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }

    
    public double estimatedRoomCharge() {
        if (room == null) return 0.0;
        return numNights * room.getPricePerNight();
    }

    @Override
    public String toString() {
        return "Reservation{ref='" + reservationRef + "', status=" + status +
               ", nights=" + numNights + "}";
    }
}
