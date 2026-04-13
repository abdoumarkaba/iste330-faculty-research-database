// Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
// Date: 2026-04-13 | ISTE 330

package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PublicUserDAO {

    public List<String> searchByKeyword(String keyword) {
        List<String> results = new ArrayList<>();
        String sql = "(SELECT 'faculty' AS type, f.first_name, f.last_name, f.email " +
                     "FROM faculty f JOIN faculty_keywords fk ON f.faculty_id = fk.faculty_id " +
                     "WHERE fk.keyword LIKE ?) " +
                     "UNION " +
                     "(SELECT 'student' AS type, s.first_name, s.last_name, s.email " +
                     "FROM students s JOIN student_keywords sk ON s.student_id = sk.student_id " +
                     "WHERE sk.keyword LIKE ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(rs.getString("type") + " | " +
                            rs.getString("first_name") + " " + rs.getString("last_name") +
                            " | " + rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<String> getAllEntries() {
        List<String> results = new ArrayList<>();
        String sql = "(SELECT 'faculty' AS type, first_name, last_name, email FROM faculty) " +
                     "UNION " +
                     "(SELECT 'student' AS type, first_name, last_name, email FROM students)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                results.add(rs.getString("type") + " | " +
                            rs.getString("first_name") + " " + rs.getString("last_name") +
                            " | " + rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}
