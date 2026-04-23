// Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
// Date: 2026-04-13 | ISTE 330

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Utility class for secure password hashing using PBKDF2.
 * Provides password hashing, verification, and strength validation.
 * 
 * Security Features:
 * - PBKDF2 with SHA-256 for strong password hashing
 * - Random salt generation for each password
 * - Configurable iteration count (default: 10000)
 * - Secure password strength validation
 * 
 * Note: Uses Java's built-in crypto libraries (no external dependencies required)
 */
public class PasswordUtil {
    
    private static final int SALT_LENGTH = 16; // 128 bits
    private static final int HASH_LENGTH = 32; // 256 bits
    private static final int DEFAULT_ITERATIONS = 10000;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 128;
    
    private static final SecureRandom secureRandom = new SecureRandom();
    
    /**
     * Generates a secure random salt.
     * 
     * @return Base64 encoded salt string
     */
    private static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Hashes a password using PBKDF2 with SHA-256.
     * Format: iterations:salt:hash (all Base64 encoded)
     * 
     * @param password the plain text password to hash
     * @return hashed password string
     * @throws IllegalArgumentException if password is invalid
     */
    public static String hashPassword(String password) {
        validatePassword(password);
        
        try {
            String salt = generateSalt();
            byte[] hash = hashPassword(password, salt, DEFAULT_ITERATIONS);
            
            // Format: iterations:salt:hash
            return DEFAULT_ITERATIONS + ":" + salt + ":" + Base64.getEncoder().encodeToString(hash);
            
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
    
    /**
     * Internal method to hash password with specific parameters.
     */
    private static byte[] hashPassword(String password, String salt, int iterations) 
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        
        byte[] saltBytes = Base64.getDecoder().decode(salt);
        
        KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, iterations, HASH_LENGTH * 8);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        
        return factory.generateSecret(spec).getEncoded();
    }
    
    /**
     * Verifies a password against a stored hash.
     * 
     * @param password the plain text password to verify
     * @param storedHash the stored hash string
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null || storedHash.isEmpty()) {
            return false;
        }
        
        try {
            // Parse the stored hash
            String[] parts = storedHash.split(":");
            if (parts.length != 3) {
                return false;
            }
            
            int iterations = Integer.parseInt(parts[0]);
            String salt = parts[1];
            String storedHashBase64 = parts[2];
            
            // Hash the provided password with the same parameters
            byte[] hash = hashPassword(password, salt, iterations);
            String computedHash = Base64.getEncoder().encodeToString(hash);
            
            // Compare hashes (constant time comparison to prevent timing attacks)
            return constantTimeEquals(storedHashBase64, computedHash);
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Validates password strength and format.
     * 
     * @param password the password to validate
     * @throws IllegalArgumentException if password doesn't meet requirements
     */
    public static void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }
        
        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                "Password must not exceed " + MAX_PASSWORD_LENGTH + " characters");
        }
        
        // Check for at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }
        
        // Check for at least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
        
        // Check for at least one digit
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }
        
        // Check for at least one special character
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character");
        }
    }
    
    /**
     * Validates password strength without throwing exceptions.
     * Returns a message describing what's wrong, or null if valid.
     * 
     * @param password the password to check
     * @return error message if invalid, null if valid
     */
    public static String checkPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty";
        }
        
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "Password must be at least " + MIN_PASSWORD_LENGTH + " characters";
        }
        
        if (password.length() > MAX_PASSWORD_LENGTH) {
            return "Password is too long (max " + MAX_PASSWORD_LENGTH + " characters)";
        }
        
        if (!password.matches(".*[A-Z].*")) {
            return "Password needs uppercase letter";
        }
        
        if (!password.matches(".*[a-z].*")) {
            return "Password needs lowercase letter";
        }
        
        if (!password.matches(".*\\d.*")) {
            return "Password needs a digit";
        }
        
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            return "Password needs a special character";
        }
        
        return null; // Password is valid
    }
    
    /**
     * Checks if a stored password hash needs to be upgraded.
     * (e.g., if using fewer iterations than current default)
     * 
     * @param storedHash the stored hash to check
     * @return true if password should be rehashed with current settings
     */
    public static boolean needsRehash(String storedHash) {
        if (storedHash == null || storedHash.isEmpty()) {
            return true;
        }
        
        try {
            String[] parts = storedHash.split(":");
            if (parts.length != 3) {
                return true;
            }
            
            int iterations = Integer.parseInt(parts[0]);
            return iterations < DEFAULT_ITERATIONS;
            
        } catch (NumberFormatException e) {
            return true;
        }
    }
    
    /**
     * Constant-time string comparison to prevent timing attacks.
     * 
     * @param a first string
     * @param b second string
     * @return true if strings are equal
     */
    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        
        return result == 0;
    }
    
    /**
     * Generates a random temporary password.
     * Useful for password reset functionality.
     * 
     * @param length the desired password length
     * @return a random password meeting strength requirements
     */
    public static String generateTemporaryPassword(int length) {
        if (length < MIN_PASSWORD_LENGTH) {
            length = MIN_PASSWORD_LENGTH;
        }
        
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*";
        String all = upper + lower + digits + special;
        
        StringBuilder password = new StringBuilder(length);
        
        // Ensure at least one of each required type
        password.append(upper.charAt(secureRandom.nextInt(upper.length())));
        password.append(lower.charAt(secureRandom.nextInt(lower.length())));
        password.append(digits.charAt(secureRandom.nextInt(digits.length())));
        password.append(special.charAt(secureRandom.nextInt(special.length())));
        
        // Fill the rest randomly
        for (int i = 4; i < length; i++) {
            password.append(all.charAt(secureRandom.nextInt(all.length())));
        }
        
        // Shuffle the password
        char[] chars = password.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = secureRandom.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        
        return new String(chars);
    }
}
