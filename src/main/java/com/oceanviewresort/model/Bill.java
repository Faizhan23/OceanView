package com.oceanviewresort.model;

import java.time.LocalDateTime;



public class Bill {

    private int           billId;
    private int           reservationId;
    private Reservation   reservation;    
    private double        roomCharge;
    private double        taxAmount;
    private double        discount;
    private double        totalAmount;
    private double        taxRate;
    private boolean       paid;
    private LocalDateTime generatedAt;
    private LocalDateTime paidAt;

    public Bill() {
        this.taxRate = 10.0;
    }

    
    public int    getBillId()          { return billId; }
    public void   setBillId(int v)     { this.billId = v; }

    public int    getReservationId()        { return reservationId; }
    public void   setReservationId(int v)   { this.reservationId = v; }

    public Reservation getReservation()             { return reservation; }
    public void        setReservation(Reservation v){ this.reservation = v; }

    public double getRoomCharge()           { return roomCharge; }
    public void   setRoomCharge(double v)   { this.roomCharge = v; }

    public double getTaxAmount()            { return taxAmount; }
    public void   setTaxAmount(double v)    { this.taxAmount = v; }

    public double getDiscount()             { return discount; }
    public void   setDiscount(double v)     { this.discount = v; }

    public double getTotalAmount()          { return totalAmount; }
    public void   setTotalAmount(double v)  { this.totalAmount = v; }

    public double getTaxRate()              { return taxRate; }
    public void   setTaxRate(double v)      { this.taxRate = v; }

    public boolean isPaid()                { return paid; }
    public void    setPaid(boolean v)      { this.paid = v; }

    public LocalDateTime getGeneratedAt()             { return generatedAt; }
    public void          setGeneratedAt(LocalDateTime v){ this.generatedAt = v; }

    public LocalDateTime getPaidAt()             { return paidAt; }
    public void          setPaidAt(LocalDateTime v){ this.paidAt = v; }

    /** Price before tax . */
    public double getSubtotal() { return roomCharge - discount; }

    @Override
    public String toString() {
        return "Bill{billId=" + billId + ", total=" + totalAmount + ", paid=" + paid + "}";
    }
}
