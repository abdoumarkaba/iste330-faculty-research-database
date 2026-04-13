// Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
// Date: 2026-04-13 | ISTE 330

package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DBConnection {
    private static String host;
    private static String user;
    private static String password;

    static {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("db.properties not found in classpath");
            }
            Properties prop = new Properties();
            prop.load(input);
            host = prop.getProperty("host");
            user = prop.getProperty("user");
            password = prop.getProperty("password");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://" + host + ":3306/faculty_research_db?useSSL=false&serverTimezone=UTC";
        return DriverManager.getConnection(url, user, password);
    }
}
