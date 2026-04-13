// Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
// Date: 2026-04-13 | ISTE 330

package util;

/**
 * Utility class for input validation.
 * All validation is performed at the data layer entry point.
 */
public class ValidationUtil {

    private static final int MAX_KEYWORD_LENGTH = 50;
    private static final int MIN_KEYWORD_WORDS = 1;
    private static final int MAX_KEYWORD_WORDS = 3;
    private static final int FACULTY_KEYWORD_COUNT = 3;

    /**
     * Validates that a keyword is 1-3 words and within length limits.
     * @param keyword the keyword to validate
     * @return true if valid
     * @throws IllegalArgumentException if invalid
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

        String[] words = trimmed.split("\\s+");
        if (words.length < MIN_KEYWORD_WORDS || words.length > MAX_KEYWORD_WORDS) {
            throw new IllegalArgumentException(
                "Keyword must be " + MIN_KEYWORD_WORDS + " to " + MAX_KEYWORD_WORDS + " words, found " + words.length);
        }

        return true;
    }

    /**
     * Validates an email address contains @ and a domain.
     * @param email the email to validate
     * @return true if valid
     * @throws IllegalArgumentException if invalid
     */
    public static boolean validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        String trimmed = email.trim();
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
}
