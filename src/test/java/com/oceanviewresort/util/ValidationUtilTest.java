package com.oceanviewresort.util;

import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import util.ValidationUtil;

@DisplayName("ValidationUtil Tests")
class ValidationUtilTest {
   ValidationUtilTest() {
   }

   @Test
   @DisplayName("isNotBlank: null returns false")
   void isNotBlank_null_returnsFalse() {
      Assertions.assertFalse(ValidationUtil.isNotBlank((String)null));
   }

   @Test
   @DisplayName("isNotBlank: empty string returns false")
   void isNotBlank_empty_returnsFalse() {
      Assertions.assertFalse(ValidationUtil.isNotBlank(""));
   }

   @Test
   @DisplayName("isNotBlank: whitespace-only returns false")
   void isNotBlank_whitespace_returnsFalse() {
      Assertions.assertFalse(ValidationUtil.isNotBlank("   "));
   }

   @Test
   @DisplayName("isNotBlank: non-empty string returns true")
   void isNotBlank_nonEmpty_returnsTrue() {
      Assertions.assertTrue(ValidationUtil.isNotBlank("Hello"));
   }

   @ParameterizedTest
   @ValueSource(
      strings = {"John Smith", "Maria O'Brien", "Jean-Paul", "Aarav"}
   )
   @DisplayName("isValidGuestName: valid names return true")
   void isValidGuestName_validNames_returnsTrue(String name) {
      Assertions.assertTrue(ValidationUtil.isValidGuestName(name));
   }

   @ParameterizedTest
   @ValueSource(
      strings = {"J", "J0hn!", "12345", "", "  "}
   )
   @DisplayName("isValidGuestName: invalid names return false")
   void isValidGuestName_invalidNames_returnsFalse(String name) {
      Assertions.assertFalse(ValidationUtil.isValidGuestName(name));
   }

   @ParameterizedTest
   @ValueSource(
      strings = {"+94771234567", "0771234567", "+12025551234", "9999999"}
   )
   @DisplayName("isValidPhone: valid phone numbers return true")
   void isValidPhone_validNumbers_returnsTrue(String phone) {
      Assertions.assertTrue(ValidationUtil.isValidPhone(phone));
   }

   @ParameterizedTest
   @ValueSource(
      strings = {"123", "abc-def", "phone", ""}
   )
   @DisplayName("isValidPhone: invalid phone numbers return false")
   void isValidPhone_invalidNumbers_returnsFalse(String phone) {
      Assertions.assertFalse(ValidationUtil.isValidPhone(phone));
   }

   @Test
   @DisplayName("isValidEmail: null/empty is valid (optional field)")
   void isValidEmail_nullOrEmpty_returnsTrue() {
      Assertions.assertTrue(ValidationUtil.isValidEmail((String)null));
      Assertions.assertTrue(ValidationUtil.isValidEmail(""));
   }

   @ParameterizedTest
   @ValueSource(
      strings = {"user@example.com", "test.email+filter@domain.co.uk"}
   )
   @DisplayName("isValidEmail: valid emails return true")
   void isValidEmail_validEmails_returnsTrue(String email) {
      Assertions.assertTrue(ValidationUtil.isValidEmail(email));
   }

   @ParameterizedTest
   @ValueSource(
      strings = {"not-an-email", "missing@", "@nodomain", "spaces @test.com"}
   )
   @DisplayName("isValidEmail: invalid emails return false")
   void isValidEmail_invalidEmails_returnsFalse(String email) {
      Assertions.assertFalse(ValidationUtil.isValidEmail(email));
   }

   @Test
   @DisplayName("isValidDate: valid ISO date returns true")
   void isValidDate_validISODate_returnsTrue() {
      Assertions.assertTrue(ValidationUtil.isValidDate("2025-12-25"));
   }

   @ParameterizedTest
   @ValueSource(
      strings = {"25/12/2025", "2025-13-01", "notadate", "", "2025-02-30"}
   )
   @DisplayName("isValidDate: invalid dates return false")
   void isValidDate_invalidDates_returnsFalse(String date) {
      Assertions.assertFalse(ValidationUtil.isValidDate(date));
   }

   @Test
   @DisplayName("isValidDateRange: future check-in with later check-out returns true")
   void isValidDateRange_validFutureDates_returnsTrue() {
      String ci = LocalDate.now().plusDays(2L).toString();
      String co = LocalDate.now().plusDays(5L).toString();
      Assertions.assertTrue(ValidationUtil.isValidDateRange(ci, co));
   }

   @Test
   @DisplayName("isValidDateRange: past check-in returns false")
   void isValidDateRange_pastCheckin_returnsFalse() {
      String ci = LocalDate.now().minusDays(1L).toString();
      String co = LocalDate.now().plusDays(3L).toString();
      Assertions.assertFalse(ValidationUtil.isValidDateRange(ci, co));
   }

   @Test
   @DisplayName("isValidDateRange: checkout == checkin returns false")
   void isValidDateRange_sameDate_returnsFalse() {
      String date = LocalDate.now().plusDays(2L).toString();
      Assertions.assertFalse(ValidationUtil.isValidDateRange(date, date));
   }

   @Test
   @DisplayName("isValidDateRange: checkout before checkin returns false")
   void isValidDateRange_checkoutBeforeCheckin_returnsFalse() {
      String ci = LocalDate.now().plusDays(5L).toString();
      String co = LocalDate.now().plusDays(2L).toString();
      Assertions.assertFalse(ValidationUtil.isValidDateRange(ci, co));
   }

   @Test
   @DisplayName("isCheckoutAfterCheckin: checkout after checkin returns true")
   void isCheckoutAfterCheckin_valid_returnsTrue() {
      Assertions.assertTrue(ValidationUtil.isCheckoutAfterCheckin(LocalDate.now(), LocalDate.now().plusDays(1L)));
   }

   @Test
   @DisplayName("isCheckoutAfterCheckin: null inputs return false")
   void isCheckoutAfterCheckin_nullInputs_returnsFalse() {
      Assertions.assertFalse(ValidationUtil.isCheckoutAfterCheckin((LocalDate)null, LocalDate.now()));
      Assertions.assertFalse(ValidationUtil.isCheckoutAfterCheckin(LocalDate.now(), (LocalDate)null));
   }

   @ParameterizedTest
   @ValueSource(
      strings = {"admin", "john_doe", "user123", "UPPERCASE"}
   )
   @DisplayName("isValidUsername: valid usernames return true")
   void isValidUsername_valid_returnsTrue(String u) {
      Assertions.assertTrue(ValidationUtil.isValidUsername(u));
   }

   @ParameterizedTest
   @ValueSource(
      strings = {"ab", "us er", "user@name", ""}
   )
   @DisplayName("isValidUsername: invalid usernames return false")
   void isValidUsername_invalid_returnsFalse(String u) {
      Assertions.assertFalse(ValidationUtil.isValidUsername(u));
   }

   @Test
   @DisplayName("isValidPassword: 8 char password is valid")
   void isValidPassword_eightChars_returnsTrue() {
      Assertions.assertTrue(ValidationUtil.isValidPassword("Passw0rd"));
   }

   @Test
   @DisplayName("isValidPassword: less than 8 chars is invalid")
   void isValidPassword_shortPassword_returnsFalse() {
      Assertions.assertFalse(ValidationUtil.isValidPassword("Short1"));
   }

   @Test
   @DisplayName("sanitise: should escape HTML special characters")
   void sanitise_htmlChars_escapedCorrectly() {
      String input = "<script>alert('XSS')</script>";
      String expected = "&lt;script&gt;alert(&#x27;XSS&#x27;)&lt;/script&gt;";
      Assertions.assertEquals(expected, ValidationUtil.sanitise(input));
   }

   @Test
   @DisplayName("sanitise: null input returns empty string")
   void sanitise_null_returnsEmpty() {
      Assertions.assertEquals("", ValidationUtil.sanitise((String)null));
   }

   @Test
   @DisplayName("sanitise: ampersand is escaped")
   void sanitise_ampersand_escaped() {
      Assertions.assertEquals("a &amp; b", ValidationUtil.sanitise("a & b"));
   }
}
