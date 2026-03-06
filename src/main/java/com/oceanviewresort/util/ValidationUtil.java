// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public final class ValidationUtil {
   private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{7,15}$");
   private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");
   private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s'\\-]{2,100}$");

   private ValidationUtil() {
   }

   public static boolean isNotBlank(String value) {
      return value != null && !value.trim().isEmpty();
   }

   public static boolean isValidGuestName(String name) {
      return isNotBlank(name) && NAME_PATTERN.matcher(name.trim()).matches();
   }

   public static boolean isValidPhone(String phone) {
      return isNotBlank(phone) && PHONE_PATTERN.matcher(phone.trim()).matches();
   }

   public static boolean isValidEmail(String email) {
      return email != null && !email.trim().isEmpty() ? EMAIL_PATTERN.matcher(email.trim()).matches() : true;
   }

   public static boolean isValidDate(String dateStr) {
      if (!isNotBlank(dateStr)) {
         return false;
      } else {
         try {
            LocalDate.parse(dateStr.trim());
            return true;
         } catch (DateTimeParseException var2) {
            return false;
         }
      }
   }

   public static boolean isValidDateRange(String checkIn, String checkOut) {
      if (isValidDate(checkIn) && isValidDate(checkOut)) {
         LocalDate ci = LocalDate.parse(checkIn.trim());
         LocalDate co = LocalDate.parse(checkOut.trim());
         return !ci.isBefore(LocalDate.now()) && co.isAfter(ci);
      } else {
         return false;
      }
   }

   public static boolean isCheckoutAfterCheckin(LocalDate checkIn, LocalDate checkOut) {
      return checkOut != null && checkIn != null && checkOut.isAfter(checkIn);
   }

   public static boolean isValidUsername(String username) {
      return isNotBlank(username) && username.trim().matches("^[a-zA-Z0-9_]{4,50}$");
   }

   public static boolean isValidPassword(String password) {
      return isNotBlank(password) && password.length() >= 8;
   }

   public static String sanitise(String input) {
      return input == null ? "" : input.trim().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#x27;");
   }

   public static boolean isPositiveInteger(String value) {
      try {
         return Integer.parseInt(value) > 0;
      } catch (NumberFormatException var2) {
         return false;
      }
   }
}
