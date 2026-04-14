// Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
// Date: 2026-04-13 | ISTE 330

package util;

import java.util.regex.Pattern;

/**
 * Utility class for input validation and security sanitization.
 * All validation is performed at the data layer entry point.
 * 
 * Security Features:
 * - SQL injection prevention through character filtering
 * - Input length restrictions to prevent buffer overflows
 * - Pattern matching to detect suspicious input
 * - ID validation to ensure positive integers only
 */
public class ValidationUtil {

    private static final int MAX_KEYWORD_LENGTH = 50;
    private static final int MIN_KEYWORD_WORDS = 1;
    private static final int MAX_KEYWORD_WORDS = 3;
    private static final int FACULTY_KEYWORD_COUNT = 3;
    
    // SQL injection prevention: characters that could be used in SQL attacks
    private static final String SQL_INJECTION_CHARS = "'\";--/*";
    
    // Maximum lengths for various input types (security and performance)
    private static final int MAX_ABSTRACT_LENGTH = 5000;
    private static final int MAX_ID_VALUE = 1000000; // Reasonable upper limit for IDs
    private static final int MIN_ID_VALUE = 1;
    
    // Pattern to detect suspicious SQL keywords in input
    private static final Pattern SQL_KEYWORDS_PATTERN = Pattern.compile(
        ".*\\b(SELECT|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|EXEC|UNION|OR|AND)\\b.*", 
        Pattern.CASE_INSENSITIVE
    );
    
    // Pattern for safe alphanumeric keywords with basic punctuation
    private static final Pattern SAFE_KEYWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s\\-_.]+$");

    /**
     * Validates that a keyword is 1-3 words, within length limits, and safe from SQL injection.
     * Security: Blocks SQL injection characters and suspicious patterns.
     * 
     * @param keyword the keyword to validate
     * @return true if valid
     * @throws IllegalArgumentException if invalid or contains suspicious characters
     */
    public static boolean validateKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Keyword cannot be empty");
        }

        String trimmed = keyword.trim();
        if (trimmed.length() > MAX_KEYWORD_LENGTH) {
            throw new IllegalArgumentException(
                "Keyword exceeds maximum length of " + MAX_KEYWORD_LENGTH + " characters");
        }

        // Security: Check for SQL injection characters
        if (containsSqlInjectionChars(trimmed)) {
            throw new IllegalArgumentException("Keyword contains invalid characters");
        }
        
        // Security: Check for suspicious SQL keywords
        if (SQL_KEYWORDS_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Keyword contains invalid patterns");
        }
        
        // Security: Validate against safe character pattern
        if (!SAFE_KEYWORD_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Keyword can only contain letters, numbers, spaces, hyphens, periods, and underscores");
        }

        String[] words = trimmed.split("\\s+");
        if (words.length < MIN_KEYWORD_WORDS || words.length > MAX_KEYWORD_WORDS) {
            throw new IllegalArgumentException(
                "Keyword must be " + MIN_KEYWORD_WORDS + " to " + MAX_KEYWORD_WORDS + " words, found " + words.length);
        }

        return true;
    }
    
    /**
     * Sanitizes a keyword for safe database usage.
     * Removes potentially dangerous characters while preserving meaningful content.
     * 
     * @param keyword the keyword to sanitize
     * @return sanitized keyword string
     */
    public static String sanitizeKeyword(String keyword) {
        if (keyword == null) return "";
        
        // Remove SQL injection characters
        String sanitized = keyword.replaceAll("['\";\\-/*]", "");
        
        // Trim and limit length
        sanitized = sanitized.trim();
        if (sanitized.length() > MAX_KEYWORD_LENGTH) {
            sanitized = sanitized.substring(0, MAX_KEYWORD_LENGTH);
        }
        
        return sanitized;
    }

    /**
     * Validates an email address contains @ and a domain.
     * Security: Also checks email length and basic format to prevent injection.
     * 
     * @param email the email to validate
     * @return true if valid
     * @throws IllegalArgumentException if invalid
     */
    public static boolean validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        String trimmed = email.trim();
        
        // Security: Limit email length
        if (trimmed.length() > 255) {
            throw new IllegalArgumentException("Email exceeds maximum length of 255 characters");
        }
        
        // Security: Check for SQL injection characters
        if (containsSqlInjectionChars(trimmed)) {
            throw new IllegalArgumentException("Email contains invalid characters");
        }
        
        if (!trimmed.contains("@")) {
            throw new IllegalArgumentException("Email must contain @ symbol");
        }

        String[] parts = trimmed.split("@");
        if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            throw new IllegalArgumentException("Email must have valid format: local@domain");
        }

        if (!parts[1].contains(".")) {
            throw new IllegalArgumentException("Email domain must contain a dot");
        }

        return true;
    }

    /**
     * Validates that a faculty has exactly 3 keywords.
     * @param keywordCount the number of keywords
     * @return true if valid
     * @throws IllegalArgumentException if invalid
     */
    public static boolean validateFacultyKeywordCount(int keywordCount) {
        if (keywordCount != FACULTY_KEYWORD_COUNT) {
            throw new IllegalArgumentException(
                "Faculty must have exactly " + FACULTY_KEYWORD_COUNT + " keywords, found " + keywordCount);
        }
        return true;
    }

    /**
     * Validates keyword count is within acceptable range for students.
     * @param keywordCount the number of keywords
     * @return true if valid
     * @throws IllegalArgumentException if invalid
     */
    public static boolean validateStudentKeywordCount(int keywordCount) {
        if (keywordCount < MIN_KEYWORD_WORDS || keywordCount > MAX_KEYWORD_WORDS) {
            throw new IllegalArgumentException(
                "Student must have " + MIN_KEYWORD_WORDS + " to " + MAX_KEYWORD_WORDS + " keywords, found " + keywordCount);
        }
        return true;
    }
    
    // ==================== SECURITY VALIDATION METHODS ====================
    
    /**
     * Validates a faculty ID is a positive integer within acceptable range.
     * Security: Prevents negative IDs and unreasonably large values.
     * 
     * @param facultyId the faculty ID to validate
     * @return true if valid
     * @throws IllegalArgumentException if invalid
     */
    public static boolean validateFacultyId(int facultyId) {
        if (facultyId < MIN_ID_VALUE) {
            throw new IllegalArgumentException("Faculty ID must be positive, got: " + facultyId);
        }
        if (facultyId > MAX_ID_VALUE) {
            throw new IllegalArgumentException("Faculty ID exceeds maximum allowed value: " + MAX_ID_VALUE);
        }
        return true;
    }
    
    /**
     * Validates a student ID is a positive integer within acceptable range.
     * Security: Prevents negative IDs and unreasonably large values.
     * 
     * @param studentId the student ID to validate
     * @return true if valid
     * @throws IllegalArgumentException if invalid
     */
    public static boolean validateStudentId(int studentId) {
        if (studentId < MIN_ID_VALUE) {
            throw new IllegalArgumentException("Student ID must be positive, got: " + studentId);
        }
        if (studentId > MAX_ID_VALUE) {
            throw new IllegalArgumentException("Student ID exceeds maximum allowed value: " + MAX_ID_VALUE);
        }
        return true;
    }
    
    /**
     * Validates an abstract ID is a positive integer.
     * Security: Ensures only valid abstract IDs are processed.
     * 
     * @param abstractId the abstract ID to validate
     * @return true if valid
     * @throws IllegalArgumentException if invalid
     */
    public static boolean validateAbstractId(int abstractId) {
        if (abstractId < MIN_ID_VALUE) {
            throw new IllegalArgumentException("Abstract ID must be positive, got: " + abstractId);
        }
        if (abstractId > MAX_ID_VALUE) {
            throw new IllegalArgumentException("Abstract ID exceeds maximum allowed value: " + MAX_ID_VALUE);
        }
        return true;
    }
    
    /**
     * Validates abstract text is safe for database storage.
     * Security: Prevents SQL injection and limits length to prevent buffer issues.
     * 
     * @param abstractText the abstract text to validate
     * @return true if valid
     * @throws IllegalArgumentException if invalid or contains suspicious content
     */
    public static boolean validateAbstractText(String abstractText) {
        if (abstractText == null || abstractText.trim().isEmpty()) {
            throw new IllegalArgumentException("Abstract text cannot be empty");
        }
        
        String trimmed = abstractText.trim();
        
        // Security: Limit length to prevent buffer overflow issues
        if (trimmed.length() > MAX_ABSTRACT_LENGTH) {
            throw new IllegalArgumentException(
                "Abstract text exceeds maximum length of " + MAX_ABSTRACT_LENGTH + " characters");
        }
        
        // Security: Check for SQL injection characters
        if (containsSqlInjectionChars(trimmed)) {
            throw new IllegalArgumentException("Abstract text contains invalid characters");
        }
        
        return true;
    }
    
    /**
     * Validates an abstract type is one of the allowed values.
     * Security: Prevents arbitrary type values that could cause injection.
     * 
     * @param abstractType the abstract type to validate
     * @return true if valid
     * @throws IllegalArgumentException if invalid
     */
    public static boolean validateAbstractType(String abstractType) {
        if (abstractType == null || abstractType.trim().isEmpty()) {
            throw new IllegalArgumentException("Abstract type cannot be empty");
        }
        
        String trimmed = abstractType.trim().toLowerCase();
        
        // Only allow specific enum values from the database schema
        if (!trimmed.equals("book") && !trimmed.equals("speaking")) {
            throw new IllegalArgumentException("Abstract type must be 'book' or 'speaking'");
        }
        
        return true;
    }
    
    /**
     * Validates a search keyword for safe database usage.
     * More lenient than validateKeyword() but still blocks SQL injection.
     * Security: Allows wildcards for search but blocks dangerous characters.
     * 
     * @param keyword the search keyword to validate
     * @return true if valid
     * @throws IllegalArgumentException if invalid
     */
    public static boolean validateSearchKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }
        
        String trimmed = keyword.trim();
        
        // Security: Limit length
        if (trimmed.length() > MAX_KEYWORD_LENGTH) {
            throw new IllegalArgumentException(
                "Search keyword exceeds maximum length of " + MAX_KEYWORD_LENGTH + " characters");
        }
        
        // Security: Check for SQL injection characters (allow % and _ for wildcards)
        if (trimmed.matches(".*['\";\\\\].*")) {
            throw new IllegalArgumentException("Search keyword contains invalid characters");
        }
        
        // Security: Check for suspicious SQL keywords
        if (SQL_KEYWORDS_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Search keyword contains invalid patterns");
        }
        
        return true;
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Checks if a string contains characters commonly used in SQL injection attacks.
     * 
     * @param input the string to check
     * @return true if the string contains SQL injection characters
     */
    private static boolean containsSqlInjectionChars(String input) {
        if (input == null) return false;
        
        for (char c : SQL_INJECTION_CHARS.toCharArray()) {
            if (input.indexOf(c) >= 0) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Sanitizes a string by removing SQL injection characters.
     * Use this for display purposes or when you need to clean user input.
     * 
     * @param input the string to sanitize
     * @return sanitized string with dangerous characters removed
     */
    public static String sanitizeForDisplay(String input) {
        if (input == null) return "";
        
        // Remove common SQL injection characters
        return input.replaceAll("['\";\\-/*]", "").trim();
    }
    
    /**
     * Validates that a name string contains only valid characters.
     * Security: Prevents injection while allowing international names.
     * 
     * @param name the name to validate
     * @param fieldName the field name for error messages (e.g., "First name", "Last name")
     * @return true if valid
     * @throws IllegalArgumentException if invalid
     */
    public static boolean validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        
        String trimmed = name.trim();
        
        // Security: Limit length
        if (trimmed.length() > 100) {
            throw new IllegalArgumentException(fieldName + " exceeds maximum length of 100 characters");
        }
        
        // Security: Check for SQL injection characters
        if (containsSqlInjectionChars(trimmed)) {
            throw new IllegalArgumentException(fieldName + " contains invalid characters");
        }
        
        // Allow letters, spaces, hyphens, and apostrophes for names like O'Connor
        if (!trimmed.matches("^[a-zA-Z\\s\\-'\\.]+$")) {
            throw new IllegalArgumentException(fieldName + " contains invalid characters");
        }
        
        return true;
    }
}
