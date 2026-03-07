// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.dao;

import com.oceanviewresort.model.User;
import java.util.Optional;

public interface UserDAO {
   Optional<User> findByUsername(String var1);

   Optional<User> findById(int var1);

   boolean existsByUsername(String var1);
}
