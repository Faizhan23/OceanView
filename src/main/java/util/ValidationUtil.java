package util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Server-side input validation helper for reservation form data.
 */
public class ValidationUtil {

    /**
     * Validates all reservation fields.
     * Returns a list of error messages; empty list means everything is valid.
     */
    public static List<String> validateReservation(
            String guestName, String address, String contactNumber,
            String roomType, String checkInStr, String checkOutStr) {

        List<String> errors = new ArrayList<>();

        // Guest Name
        if (isBlank(guestName)) {
            errors.add("Guest name is required.");
        } else if (guestName.trim().length() < 2) {
            errors.add("Guest name must be at least 2 characters.");
        }

        // Address
        if (isBlank(address)) {
            errors.add("Address is required.");
        }

        // Contact Number – must be 10 digits (Sri Lanka format)
        if (isBlank(contactNumber)) {
            errors.add("Contact number is required.");
        } else if (!contactNumber.trim().matches("^[0-9]{10}$")) {
            errors.add("Contact number must be exactly 10 digits.");
        }

        // Room Type
        if (isBlank(roomType) ||
            (!roomType.equals("Single") && !roomType.equals("Double") && !roomType.equals("Deluxe"))) {
            errors.add("Please select a valid room type (Single, Double, or Deluxe).");
        }

        // Dates
        LocalDate checkIn  = null;
        LocalDate checkOut = null;

        if (isBlank(checkInStr)) {
            errors.add("Check-in date is required.");
        } else {
            try {
                checkIn = LocalDate.parse(checkInStr.trim());
            } catch (DateTimeParseException e) {
                errors.add("Invalid check-in date format. Use YYYY-MM-DD.");
            }
        }

        if (isBlank(checkOutStr)) {
            errors.add("Check-out date is required.");
        } else {
            try {
                checkOut = LocalDate.parse(checkOutStr.trim());
            } catch (DateTimeParseException e) {
                errors.add("Invalid check-out date format. Use YYYY-MM-DD.");
            }
        }

        // Date logic
        if (checkIn != null && checkOut != null) {
            if (!checkOut.isAfter(checkIn)) {
                errors.add("Check-out date must be after check-in date.");
            }
            if (checkIn.isBefore(LocalDate.now())) {
                errors.add("Check-in date cannot be in the past.");
            }
        }

        return errors;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
