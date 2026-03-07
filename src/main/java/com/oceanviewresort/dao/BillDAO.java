// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.dao;

import com.oceanviewresort.model.Bill;
import java.util.Optional;

public interface BillDAO {
   Optional<Bill> findByReservationId(int var1);

   boolean markAsPaid(int var1);
}
