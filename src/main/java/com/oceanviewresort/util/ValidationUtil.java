package com.oceanviewresort.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Utility class for input validation across the application.
 * Stateless – all methods are static.
 */
public final class ValidationUtil {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{7,15}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern NAME_PATTERN  = Pattern.compile("^[a-zA-Z\\s'\\-]{2,100}$");

    /** Private constructor – utility class must not be instantiated. */
    private ValidationUtil() { }

    /**
     * Validates that a string is non-null and non-blank.
     */
    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validates a guest name (letters, spaces, hyphens, apostrophes; 2–100 chars).
     */
    public static boolean isValidGuestName(String name) {
        return isNotBlank(name) && NAME_PATTERN.matcher(name.trim()).matches();
    }

    /**
     * Validates a contact/phone number.
     */
    public static boolean isValidPhone(String phone) {
        return isNotBlank(phone) && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Validates an email address.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return true; // email is optional
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates that a date string is in ISO format (yyyy-MM-dd) and is a real date.
     */
    public static boolean isValidDate(String dateStr) {
        if (!isNotBlank(dateStr)) return false;
        try {
            LocalDate.parse(dateStr.trim());
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Validates that check-in is before check-out and check-in is not in the past.
     */
    public static boolean isValidDateRange(String checkIn, String checkOut) {
        if (!isValidDate(checkIn) || !isValidDate(checkOut)) return false;
        LocalDate ci = LocalDate.parse(checkIn.trim());
        LocalDate co = LocalDate.parse(checkOut.trim());
        return !ci.isBefore(LocalDate.now()) && co.isAfter(ci);
    }

    /**
     * Validates that check-in is before check-out (bypasses past-date check – used for updates).
     */
    public static boolean isCheckoutAfterCheckin(LocalDate checkIn, LocalDate checkOut) {
        return checkOut != null && checkIn != null && checkOut.isAfter(checkIn);
    }

    /**
     * Validates a username (4–50 alphanumeric chars plus underscore).
     */
    public static boolean isValidUsername(String username) {
        return isNotBlank(username) && username.trim().matches("^[a-zA-Z0-9_]{4,50}$");
    }

    /**
     * Validates a password (minimum 8 characters).
     */
    public static boolean isValidPassword(String password) {
        return isNotBlank(password) && password.length() >= 8;
    }

    /**
     * Sanitises a string for safe display (basic XSS prevention).
     */
    public static String sanitise(String input) {
        if (input == null) return "";
        return input.trim()
                .replace("&",  "&amp;")
                .replace("<",  "&lt;")
                .replace(">",  "&gt;")
                .replace("\"", "&quot;")
                .replace("'",  "&#x27;");
    }

    /**
     * Validates that a numeric string is a positive integer.
     */
    public static boolean isPositiveInteger(String value) {
        try {
            return Integer.parseInt(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
