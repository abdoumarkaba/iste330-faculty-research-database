// Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
// Date: 2026-04-13 | ISTE 330

package exception;

/**
 * Custom exception for database operations.
 * Provides meaningful error context instead of silently swallowing exceptions.
 */
public class DatabaseException extends RuntimeException {

    private final String operation;

    public DatabaseException(String operation, String message) {
        super(message);
        this.operation = operation;
    }

    public DatabaseException(String operation, String message, Throwable cause) {
        super(message, cause);
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    @Override
    public String toString() {
        return "DatabaseException[" + operation + "]: " + getMessage();
    }
}
