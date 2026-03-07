// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.util;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

public final class ReservationRefGenerator {
   private static final AtomicInteger counter = new AtomicInteger(0);

   private ReservationRefGenerator() {
   }

   public static String generate(int sequenceNumber) {
      int year = LocalDate.now().getYear();
      return String.format("OVR-%d-%06d", year, sequenceNumber);
   }

   public static String generateNext() {
      return generate(counter.incrementAndGet());
   }

   static void resetCounter() {
      counter.set(0);
   }
}
