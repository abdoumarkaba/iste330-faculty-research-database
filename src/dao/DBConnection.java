// Author: Developer | Date: 2026-03-30 | ISTE 330

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
            Properties prop = new Properties();
            prop.load(input);
            host = prop.getProperty("host");
            user = prop.getProperty("user");
            password = prop.getProperty("password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://" + host + ":3306/faculty_research_db?useSSL=false&serverTimezone=UTC";
        return DriverManager.getConnection(url, user, password);
    }
}
