package com.example.photojournal.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    private static final int ROUNDS = 10;
    
    /**
     * Hash a password using BCrypt
     * @param password Plain text password
     * @return Hashed password
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(ROUNDS));
    }
    
    /**
     * Verify a password against a hash
     * @param password Plain text password
     * @param hash Hashed password
     * @return true if password matches hash
     */
    public static boolean verifyPassword(String password, String hash) {
        try {
            return BCrypt.checkpw(password, hash);
        } catch (Exception e) {
            return false;
        }
    }
}

