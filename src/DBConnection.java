// Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
// Date: 2026-04-13 | ISTE 330

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Database connection manager using DriverManager.
 * Loads credentials from environment variables first, falls back to properties file.
 * Simple, reliable connection management without external dependencies.
 */
public class DBConnection {
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DB = "faculty_research_db";
    
    private static String host;
    private static String port;
    private static String database;
    private static String user;
    private static String password;
    private static boolean usingEnvVars = false;

    static {
        loadConfiguration();
    }
    
    /**
     * Loads database configuration from environment variables or properties file.
     * Environment variables take precedence for security.
     */
    private static void loadConfiguration() {
        // Try environment variables first (more secure)
        String envHost = System.getenv("DB_HOST");
        String envPort = System.getenv("DB_PORT");
        String envDatabase = System.getenv("DB_NAME");
        String envUser = System.getenv("DB_USER");
        String envPassword = System.getenv("DB_PASSWORD");
        
        if (envHost != null && envUser != null && envPassword != null) {
            // Use environment variables
            host = envHost;
            port = envPort != null ? envPort : DEFAULT_PORT;
            database = envDatabase != null ? envDatabase : DEFAULT_DB;
            user = envUser;
            password = envPassword;
            usingEnvVars = true;
            System.out.println("Database configuration loaded from environment variables");
        } else {
            // Fall back to properties file
            loadFromPropertiesFile();
            System.out.println("Database configuration loaded from properties file");
        }
        
        validateConfiguration();
    }
    
    /**
     * Loads configuration from db.properties file.
     */
    private static void loadFromPropertiesFile() {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("db.properties not found in classpath and no environment variables set. " +
                                         "Please create db.properties or set DB_HOST, DB_USER, and DB_PASSWORD environment variables.");
            }
            Properties prop = new Properties();
            prop.load(input);
            
            String hostWithPort = prop.getProperty("host", DEFAULT_HOST + ":" + DEFAULT_PORT);
            if (hostWithPort.contains(":")) {
                String[] parts = hostWithPort.split(":");
                host = parts[0];
                port = parts[1];
            } else {
                host = hostWithPort;
                port = DEFAULT_PORT;
            }
            
            database = prop.getProperty("database", DEFAULT_DB);
            user = prop.getProperty("user");
            password = prop.getProperty("password");
            
            if (user == null || password == null) {
                throw new RuntimeException("Database user and password must be configured in db.properties or environment variables");
            }
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }
    
    /**
     * Validates that all required configuration is present.
     */
    private static void validateConfiguration() {
        if (host == null || host.trim().isEmpty()) {
            throw new RuntimeException("Database host is not configured");
        }
        if (user == null || user.trim().isEmpty()) {
            throw new RuntimeException("Database user is not configured");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Database password is not configured");
        }
        if (database == null || database.trim().isEmpty()) {
            database = DEFAULT_DB;
        }
        if (port == null || port.trim().isEmpty()) {
            port = DEFAULT_PORT;
        }
    }

    /**
     * Gets a database connection using DriverManager.
     * 
     * @return A valid database connection
     * @throws SQLException if connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
        
        String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true", 
                                      host, port, database);
        
        return DriverManager.getConnection(jdbcUrl, user, password);
    }
    
    /**
     * Checks if configuration was loaded from environment variables.
     * 
     * @return true if using environment variables, false if using properties file
     */
    public static boolean isUsingEnvironmentVariables() {
        return usingEnvVars;
    }
    
    /**
     * Gets the current database configuration (without password for security).
     * 
     * @return Configuration summary string
     */
    public static String getConfigurationInfo() {
        return String.format("Host: %s, Port: %s, Database: %s, User: %s, Source: %s",
                           host, port, database, user, 
                           usingEnvVars ? "Environment Variables" : "Properties File");
    }
}
