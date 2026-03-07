package com.oceanviewresort.service;

import com.oceanviewresort.dao.BillDAO;
import com.oceanviewresort.dao.ReservationDAO;
import com.oceanviewresort.model.Bill;
import com.oceanviewresort.model.Reservation;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BillingService – in-memory calculations (no DB).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BillingService Tests")
class BillingServiceTest {

    @Mock private BillDAO        billDAO;
    @Mock private ReservationDAO reservationDAO;

    private BillingService service;

    @BeforeEach
    void setUp() {
        service = new BillingService(billDAO, reservationDAO);
    }

    // ── In-memory calculation tests (no DB needed) ─────────────────────────────

    @Test
    @DisplayName("calculateBillInMemory: should compute correct room charge")
    void calculateBillInMemory_correctRoomCharge() {
        Bill bill = service.calculateBillInMemory(3, 180.00, 10.0, 0.0);

        assertEquals(540.00, bill.getRoomCharge(), 0.001, "Room charge: 3 * 180 = 540");
    }

    @Test
    @DisplayName("calculateBillInMemory: should compute correct tax")
    void calculateBillInMemory_correctTax() {
        Bill bill = service.calculateBillInMemory(3, 180.00, 10.0, 0.0);

        assertEquals(54.00, bill.getTaxAmount(), 0.001, "Tax: 540 * 10% = 54");
    }

    @Test
    @DisplayName("calculateBillInMemory: should compute correct total")
    void calculateBillInMemory_correctTotal() {
        Bill bill = service.calculateBillInMemory(3, 180.00, 10.0, 0.0);

        assertEquals(594.00, bill.getTotalAmount(), 0.001, "Total: 540 + 54 = 594");
    }

    @Test
    @DisplayName("calculateBillInMemory: discount should reduce taxable amount")
    void calculateBillInMemory_withDiscount_correctTotal() {
        // 3 nights * $180 = $540 room charge
        // discount $40 → taxable = $500
        // tax = $50
        // total = $550
        Bill bill = service.calculateBillInMemory(3, 180.00, 10.0, 40.0);

        assertEquals(540.00, bill.getRoomCharge(), 0.001);
        assertEquals(40.00,  bill.getDiscount(),   0.001);
        assertEquals(50.00,  bill.getTaxAmount(),  0.001, "Tax on $500 at 10%");
        assertEquals(550.00, bill.getTotalAmount(), 0.001, "Total after discount & tax");
    }

    @Test
    @DisplayName("calculateBillInMemory: single night standard room")
    void calculateBillInMemory_oneNightStandard() {
        Bill bill = service.calculateBillInMemory(1, 120.00, 10.0, 0.0);

        assertEquals(120.00, bill.getRoomCharge(), 0.001);
        assertEquals(12.00,  bill.getTaxAmount(),  0.001);
        assertEquals(132.00, bill.getTotalAmount(), 0.001);
    }

    @Test
    @DisplayName("calculateBillInMemory: 7 nights suite calculation")
    void calculateBillInMemory_sevenNightsSuite() {
        Bill bill = service.calculateBillInMemory(7, 280.00, 10.0, 0.0);

        assertEquals(1960.00, bill.getRoomCharge(), 0.001, "7 * 280 = 1960");
        assertEquals(196.00,  bill.getTaxAmount(),  0.001, "10% of 1960 = 196");
        assertEquals(2156.00, bill.getTotalAmount(), 0.001, "1960 + 196 = 2156");
    }

    @Test
    @DisplayName("calculateBillInMemory: zero nights should result in zero charge")
    void calculateBillInMemory_zeroNights_zeroCharge() {
        Bill bill = service.calculateBillInMemory(0, 180.00, 10.0, 0.0);

        assertEquals(0.00, bill.getRoomCharge(), 0.001);
        assertEquals(0.00, bill.getTotalAmount(), 0.001);
    }

    // ── Negative discount guard ────────────────────────────────────────────────
    @Test
    @DisplayName("calculateBill: negative discount should throw IllegalArgumentException")
    void calculateBill_negativeDiscount_throwsException() {
        when(reservationDAO.findById(1)).thenReturn(Optional.of(new Reservation()));

        assertThrows(IllegalArgumentException.class, () ->
                service.calculateBill(1, -10.0));
    }

    @Test
    @DisplayName("getBillByReservation: should return empty when no bill exists")
    void getBillByReservation_noBillExists_returnsEmpty() {
        when(billDAO.findByReservationId(5)).thenReturn(Optional.empty());

        assertTrue(service.getBillByReservation(5).isEmpty());
    }

    @Test
    @DisplayName("getBillByReservation: should return bill when found")
    void getBillByReservation_billExists_returnsBill() {
        Bill bill = new Bill();
        bill.setBillId(10);
        bill.setReservationId(5);
        when(billDAO.findByReservationId(5)).thenReturn(Optional.of(bill));
        when(reservationDAO.findById(5)).thenReturn(Optional.of(new Reservation()));

        Optional<Bill> result = service.getBillByReservation(5);

        assertTrue(result.isPresent());
        assertEquals(10, result.get().getBillId());
    }
    

    @Test
    @DisplayName("Bill.getSubtotal: should return room charge minus discount")
    void bill_getSubtotal_returnsChargeMinusDiscount() {
        Bill bill = new Bill();
        bill.setRoomCharge(540.00);
        bill.setDiscount(40.00);

        assertEquals(500.00, bill.getSubtotal(), 0.001);
    }
}
