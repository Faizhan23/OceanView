package com.oceanviewresort.util;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates unique human-readable reservation reference numbers.
 * Format: OVR-YYYY-NNNNNN  (e.g. OVR-2025-000042)
 */
public final class ReservationRefGenerator {

    private static final AtomicInteger counter = new AtomicInteger(0);

    private ReservationRefGenerator() { }

    public static String generate(int sequenceNumber) {
        int year = LocalDate.now().getYear();
        return String.format("OVR-%d-%06d", year, sequenceNumber);
    }

    public static String generateNext() {
        return generate(counter.incrementAndGet());
    }

    /**
     * Resets the counter – for testing purposes only.
     */
    static void resetCounter() {
        counter.set(0);
    }
}
