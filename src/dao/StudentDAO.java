// Author: Developer | Date: 2026-03-30 | ISTE 330

package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public List<String> searchFacultyByKeyword(String keyword) {
        List<String> results = new ArrayList<>();
        String sql = "SELECT DISTINCT f.first_name, f.last_name, f.building, f.office_number, f.email " +
                     "FROM faculty f " +
                     "LEFT JOIN faculty_keywords fk ON f.faculty_id = fk.faculty_id " +
                     "LEFT JOIN faculty_abstracts fa ON f.faculty_id = fa.faculty_id " +
                     "WHERE fk.keyword LIKE ? OR fa.abstract_text LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(rs.getString("first_name") + " " + rs.getString("last_name") +
                            " | " + rs.getString("building") + " " + rs.getString("office_number") +
                            " | " + rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<String> getAllFaculty() {
        List<String> results = new ArrayList<>();
        String sql = "SELECT first_name, last_name, building, office_number, email FROM faculty";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                results.add(rs.getString("first_name") + " " + rs.getString("last_name") +
                            " | " + rs.getString("building") + " " + rs.getString("office_number") +
                            " | " + rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public boolean isValidStudentId(int studentId) {
        String sql = "SELECT COUNT(*) FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
