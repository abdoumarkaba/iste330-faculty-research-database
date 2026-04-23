// Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
// Date: 2026-04-13 | ISTE 330

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple logging utility for the Faculty Research Database application.
 * Provides thread-safe logging to console and file with rotation support.
 * 
 * Features:
 * - Three log levels: DEBUG, INFO, ERROR
 * - Timestamped entries
 * - Automatic log file rotation (max 1MB)
 * - Thread-safe implementation
 * - No external dependencies
 */
public class SimpleLogger {
    
    public enum Level {
        DEBUG, INFO, ERROR
    }
    
    private static final String LOG_FILE = "logs/app.log";
    private static final long MAX_LOG_SIZE = 1024 * 1024; // 1MB
    private static final DateTimeFormatter TIMESTAMP_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private static Level currentLevel = Level.INFO;
    private static boolean consoleOutput = true;
    private static boolean fileOutput = true;
    private static final Object lock = new Object();
    
    // Initialize log directory
    static {
        try {
            Path logDir = Paths.get("logs");
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }
        } catch (IOException e) {
            System.err.println("Failed to create log directory: " + e.getMessage());
        }
        
        // Load log level from environment
        String envLevel = System.getenv("LOG_LEVEL");
        if (envLevel != null) {
            try {
                currentLevel = Level.valueOf(envLevel.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid LOG_LEVEL: " + envLevel + ", using INFO");
            }
        }
    }
    
    /**
     * Sets the minimum log level. Messages below this level will be ignored.
     * 
     * @param level the minimum level to log
     */
    public static void setLevel(Level level) {
        currentLevel = level;
    }
    
    /**
     * Gets the current log level.
     * 
     * @return current log level
     */
    public static Level getLevel() {
        return currentLevel;
    }
    
    /**
     * Enables or disables console output.
     * 
     * @param enabled true to enable console output
     */
    public static void setConsoleOutput(boolean enabled) {
        consoleOutput = enabled;
    }
    
    /**
     * Enables or disables file output.
     * 
     * @param enabled true to enable file output
     */
    public static void setFileOutput(boolean enabled) {
        fileOutput = enabled;
    }
    
    /**
     * Logs a DEBUG message.
     * 
     * @param message the message to log
     */
    public static void debug(String message) {
        log(Level.DEBUG, message, null);
    }
    
    /**
     * Logs a DEBUG message with exception.
     * 
     * @param message the message to log
     * @param throwable the exception to log
     */
    public static void debug(String message, Throwable throwable) {
        log(Level.DEBUG, message, throwable);
    }
    
    /**
     * Logs an INFO message.
     * 
     * @param message the message to log
     */
    public static void info(String message) {
        log(Level.INFO, message, null);
    }
    
    /**
     * Logs an INFO message with exception.
     * 
     * @param message the message to log
     * @param throwable the exception to log
     */
    public static void info(String message, Throwable throwable) {
        log(Level.INFO, message, throwable);
    }
    
    /**
     * Logs an ERROR message.
     * 
     * @param message the message to log
     */
    public static void error(String message) {
        log(Level.ERROR, message, null);
    }
    
    /**
     * Logs an ERROR message with exception.
     * 
     * @param message the message to log
     * @param throwable the exception to log
     */
    public static void error(String message, Throwable throwable) {
        log(Level.ERROR, message, throwable);
    }
    
    /**
     * Internal logging method.
     * 
     * @param level the log level
     * @param message the message to log
     * @param throwable optional exception
     */
    private static void log(Level level, String message, Throwable throwable) {
        // Check if we should log this level
        if (level.ordinal() < currentLevel.ordinal()) {
            return;
        }
        
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String threadName = Thread.currentThread().getName();
        String formattedMessage = String.format("[%s] [%s] [%s] %s",
            timestamp, level, threadName, message);
        
        synchronized (lock) {
            // Console output
            if (consoleOutput) {
                if (level == Level.ERROR) {
                    System.err.println(formattedMessage);
                    if (throwable != null) {
                        throwable.printStackTrace(System.err);
                    }
                } else {
                    System.out.println(formattedMessage);
                    if (throwable != null) {
                        throwable.printStackTrace(System.out);
                    }
                }
            }
            
            // File output
            if (fileOutput) {
                writeToFile(formattedMessage, throwable);
            }
        }
    }
    
    /**
     * Writes a log entry to the file with rotation support.
     * 
     * @param message the formatted message
     * @param throwable optional exception
     */
    private static void writeToFile(String message, Throwable throwable) {
        try {
            // Check if rotation is needed
            Path logPath = Paths.get(LOG_FILE);
            if (Files.exists(logPath)) {
                long size = Files.size(logPath);
                if (size > MAX_LOG_SIZE) {
                    rotateLogFile();
                }
            }
            
            // Write to file
            try (FileWriter fw = new FileWriter(LOG_FILE, true);
                 PrintWriter pw = new PrintWriter(fw)) {
                pw.println(message);
                if (throwable != null) {
                    throwable.printStackTrace(pw);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
    
    /**
     * Rotates the log file by renaming it with a timestamp.
     */
    private static void rotateLogFile() {
        try {
            String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path oldLog = Paths.get(LOG_FILE);
            Path newLog = Paths.get("logs/app_" + timestamp + ".log");
            Files.move(oldLog, newLog);
        } catch (IOException e) {
            System.err.println("Failed to rotate log file: " + e.getMessage());
        }
    }
    
    /**
     * Formats an exception for logging.
     * 
     * @param throwable the exception to format
     * @return formatted string
     */
    public static String formatException(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.getClass().getName()).append(": ")
          .append(throwable.getMessage()).append("\n");
        
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        
        Throwable cause = throwable.getCause();
        if (cause != null) {
            sb.append("Caused by: ").append(formatException(cause));
        }
        
        return sb.toString();
    }
    
    /**
     * Gets the current log file path.
     * 
     * @return log file path
     */
    public static String getLogFilePath() {
        return LOG_FILE;
    }
    
    /**
     * Cleans up old log files, keeping only the most recent ones.
     * 
     * @param keepCount number of log files to keep
     */
    public static void cleanupOldLogs(int keepCount) {
        try {
            Path logDir = Paths.get("logs");
            if (!Files.exists(logDir)) {
                return;
            }
            
            Files.list(logDir)
                .filter(p -> p.getFileName().toString().startsWith("app_"))
                .filter(p -> p.getFileName().toString().endsWith(".log"))
                .sorted((p1, p2) -> {
                    try {
                        return Files.getLastModifiedTime(p2).compareTo(Files.getLastModifiedTime(p1));
                    } catch (IOException e) {
                        return 0;
                    }
                })
                .skip(keepCount)
                .forEach(p -> {
                    try {
                        Files.delete(p);
                        info("Deleted old log file: " + p.getFileName());
                    } catch (IOException e) {
                        error("Failed to delete old log file: " + p.getFileName(), e);
                    }
                });
        } catch (IOException e) {
            error("Failed to cleanup old logs", e);
        }
    }
}
