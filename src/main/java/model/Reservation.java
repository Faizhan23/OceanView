package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class Reservation {

    // Room type constants
    public static final String SINGLE = "Single";
    public static final String DOUBLE = "Double";
    public static final String DELUXE = "Deluxe";

    // Nightly rates (LKR)
    public static final double RATE_SINGLE = 5000.0;
    public static final double RATE_DOUBLE = 8000.0;
    public static final double RATE_DELUXE = 12000.0;

    private String reservationNumber;
    private String guestName;
    private String address;
    private String contactNumber;
    private String roomType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    // Default constructor
    public Reservation() {
    }

    // Parameterised constructor
    public Reservation(String reservationNumber, String guestName, String address,
            String contactNumber, String roomType,
            LocalDate checkInDate, LocalDate checkOutDate) {
        this.reservationNumber = reservationNumber;
        this.guestName = guestName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.roomType = roomType;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    // ---------------------------------------------------------------
    // Calculated helpers
    // ---------------------------------------------------------------

    /** Returns the number of nights between check-in and check-out. */
    public long getNumberOfNights() {
        if (checkInDate == null || checkOutDate == null)
            return 0;
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    /** Returns the nightly rate for the current room type. */
    public double getNightlyRate() {
        if (roomType == null)
            return 0;
        switch (roomType) {
            case SINGLE:
                return RATE_SINGLE;
            case DOUBLE:
                return RATE_DOUBLE;
            case DELUXE:
                return RATE_DELUXE;
            default:
                return 0;
        }
    }

    /** Returns the total bill (nights × rate). */
    public double getTotalAmount() {
        return getNumberOfNights() * getNightlyRate();
    }

    
    public String getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(String reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    
    public String toFileString() {
        return reservationNumber + "|" + guestName + "|" + address + "|"
                + contactNumber + "|" + roomType + "|"
                + checkInDate.toString() + "|" + checkOutDate.toString();
    }

    public static Reservation fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 7)
            return null;
        return new Reservation(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                parts[4].trim(),
                LocalDate.parse(parts[5].trim()),
                LocalDate.parse(parts[6].trim()));
    }

    @Override
    public String toString() {
        return "Reservation{" + reservationNumber + ", " + guestName + ", " + roomType
                + ", " + checkInDate + " -> " + checkOutDate + "}";
    }
}
