// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {
   private static final int BCRYPT_ROUNDS = 12;

   private PasswordUtil() {
   }

   public static String hashPassword(String plainPassword) {
	   return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
   }

   public static boolean verifyPassword(String plainPassword, String hashedPassword) {
      try {
         return BCrypt.checkpw(plainPassword, hashedPassword);
      } catch (IllegalArgumentException var3) {
         return false;
      }
   }
}
