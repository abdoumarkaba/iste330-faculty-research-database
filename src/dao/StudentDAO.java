// Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
// Date: 2026-04-13 | ISTE 330

package dao;

import model.Student;
import model.Faculty;
import exception.DatabaseException;
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
            throw new DatabaseException("searchFacultyByKeyword",
                "Failed to search faculty by keyword: " + keyword, e);
        }
        return results;
    }

    /**
     * Searches faculty by keyword, returning Faculty model objects.
     */
    public List<Faculty> searchFacultyByKeywordModels(String keyword) {
        List<Faculty> results = new ArrayList<>();
        String sql = "SELECT DISTINCT f.faculty_id, f.first_name, f.last_name, f.building, f.office_number, f.email " +
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
                results.add(new Faculty(
                    rs.getInt("faculty_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("building"),
                    rs.getString("office_number"),
                    rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException("searchFacultyByKeywordModels",
                "Failed to search faculty by keyword: " + keyword, e);
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
            throw new DatabaseException("getAllFaculty",
                "Failed to get all faculty", e);
        }
        return results;
    }

    /**
     * Gets all faculty as model objects.
     */
    public List<Faculty> getAllFacultyModels() {
        List<Faculty> results = new ArrayList<>();
        String sql = "SELECT faculty_id, first_name, last_name, building, office_number, email FROM faculty";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                results.add(new Faculty(
                    rs.getInt("faculty_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("building"),
                    rs.getString("office_number"),
                    rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException("getAllFacultyModels",
                "Failed to get all faculty models", e);
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
            throw new DatabaseException("isValidStudentId",
                "Failed to validate student ID " + studentId, e);
        }
        return false;
    }

    public List<String> getStudentKeywords(int studentId) {
        List<String> keywords = new ArrayList<>();
        String sql = "SELECT keyword FROM student_keywords WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                keywords.add(rs.getString("keyword"));
            }
        } catch (SQLException e) {
            throw new DatabaseException("getStudentKeywords",
                "Failed to get keywords for student ID " + studentId, e);
        }
        return keywords;
    }

    public Student getStudentById(int studentId) {
        String sql = "SELECT student_id, first_name, last_name, email FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new model.Student(studentId,
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"));
            }
        } catch (SQLException e) {
            throw new DatabaseException("getStudentById",
                "Failed to get student by ID " + studentId, e);
        }
        return null;
    }
}
