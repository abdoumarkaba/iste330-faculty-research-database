// Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
// Date: 2026-04-13 | ISTE 330

package dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Database connection manager with HikariCP connection pooling.
 * Loads credentials from environment variables first, falls back to properties file.
 * Provides efficient connection reuse and monitoring capabilities.
 */
public class DBConnection {
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DB = "faculty_research_db";
    private static final int DEFAULT_POOL_SIZE = 5;
    private static final int DEFAULT_TIMEOUT = 30000; // 30 seconds
    
    private static String host;
    private static String port;
    private static String database;
    private static String user;
    private static String password;
    private static boolean usingEnvVars = false;
    
    private static HikariDataSource dataSource;
    private static int poolSize;
    private static int connectionTimeout;

    static {
        loadConfiguration();
        initializePool();
        registerShutdownHook();
    }
    
    /**
     * Loads database configuration from environment variables or properties file.
     * Environment variables take precedence for security.
     * Also loads connection pool configuration.
     */
    private static void loadConfiguration() {
        // Try environment variables first (more secure)
        String envHost = System.getenv("DB_HOST");
        String envPort = System.getenv("DB_PORT");
        String envDatabase = System.getenv("DB_NAME");
        String envUser = System.getenv("DB_USER");
        String envPassword = System.getenv("DB_PASSWORD");
        
        // Load pool configuration from environment
        String envPoolSize = System.getenv("DB_POOL_SIZE");
        String envTimeout = System.getenv("DB_CONNECTION_TIMEOUT");
        
        poolSize = DEFAULT_POOL_SIZE;
        connectionTimeout = DEFAULT_TIMEOUT;
        
        if (envPoolSize != null) {
            try {
                poolSize = Integer.parseInt(envPoolSize);
            } catch (NumberFormatException e) {
                System.out.println("WARNING: Invalid DB_POOL_SIZE, using default: " + DEFAULT_POOL_SIZE);
            }
        }
        
        if (envTimeout != null) {
            try {
                connectionTimeout = Integer.parseInt(envTimeout);
            } catch (NumberFormatException e) {
                System.out.println("WARNING: Invalid DB_CONNECTION_TIMEOUT, using default: " + DEFAULT_TIMEOUT);
            }
        }
        
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
            System.out.println("WARNING: Database configuration loaded from properties file. " +
                             "Consider using environment variables for better security.");
        }
        
        validateConfiguration();
    }
    
    /**
     * Initializes the HikariCP connection pool.
     */
    private static void initializePool() {
        try {
            HikariConfig config = new HikariConfig();
            
            String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true", 
                                          host, port, database);
            
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(user);
            config.setPassword(password);
            
            // Pool configuration
            config.setMaximumPoolSize(poolSize);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(connectionTimeout);
            config.setIdleTimeout(600000); // 10 minutes
            config.setMaxLifetime(1800000); // 30 minutes
            config.setPoolName("FacultyResearchPool");
            
            // Connection validation
            config.setConnectionTestQuery("SELECT 1");
            config.setValidationTimeout(5000);
            
            // Performance settings
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            
            dataSource = new HikariDataSource(config);
            
            System.out.println("HikariCP connection pool initialized (size: " + poolSize + ")");
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize connection pool", e);
        }
    }
    
    /**
     * Registers a shutdown hook to close the pool gracefully.
     */
    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (dataSource != null && !dataSource.isClosed()) {
                System.out.println("Closing connection pool...");
                dataSource.close();
            }
        }));
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
     * Gets a database connection from the connection pool.
     * Connections are automatically returned to the pool when closed.
     * 
     * @return A valid database connection from the pool
     * @throws SQLException if connection cannot be obtained from pool
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("Connection pool is not initialized or has been closed");
        }
        return dataSource.getConnection();
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
        return String.format("Host: %s, Port: %s, Database: %s, User: %s, Source: %s, Pool Size: %d",
                           host, port, database, user, 
                           usingEnvVars ? "Environment Variables" : "Properties File",
                           poolSize);
    }
    
    /**
     * Gets connection pool statistics for monitoring.
     * 
     * @return Pool statistics string
     */
    public static String getPoolStatistics() {
        if (dataSource == null || dataSource.isClosed()) {
            return "Pool not initialized";
        }
        
        HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();
        if (poolMXBean == null) {
            return "Pool statistics unavailable";
        }
        
        return String.format("Pool: %s | Active: %d | Idle: %d | Waiting: %d | Total: %d",
                           dataSource.getPoolName(),
                           poolMXBean.getActiveConnections(),
                           poolMXBean.getIdleConnections(),
                           poolMXBean.getThreadsAwaitingConnection(),
                           poolMXBean.getTotalConnections());
    }
    
    /**
     * Checks if the connection pool is healthy.
     * 
     * @return true if pool is active and responsive
     */
    public static boolean isPoolHealthy() {
        if (dataSource == null || dataSource.isClosed()) {
            return false;
        }
        
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Gets the connection pool size.
     * 
     * @return maximum pool size
     */
    public static int getPoolSize() {
        return poolSize;
    }
    
    /**
     * Closes the connection pool. Should be called on application shutdown.
     */
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
