package com.oceanviewresort.dao;

import com.oceanviewresort.model.Bill;
import java.util.Optional;

/**
 * This DAO interface for Bill smooth operations. for "my note"
 */

public interface BillDAO {
    Optional<Bill> findByReservationId(int reservationId);
    boolean markAsPaid(int reservationId);
}
