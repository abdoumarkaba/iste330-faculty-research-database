// Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
// Date: 2026-04-13 | ISTE 330

package dao;

import model.Faculty;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacultyDAO {

    public boolean insertAbstract(int facultyId, String abstractType, String abstractText) {
        String sql = "INSERT INTO faculty_abstracts (faculty_id, abstract_type, abstract_text) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, facultyId);
            pstmt.setString(2, abstractType);
            pstmt.setString(3, abstractText);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAbstract(int abstractId, String abstractText) {
        String sql = "UPDATE faculty_abstracts SET abstract_text = ? WHERE abstract_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, abstractText);
            pstmt.setInt(2, abstractId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAbstract(int abstractId) {
        String sql = "DELETE FROM faculty_abstracts WHERE abstract_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, abstractId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getFacultyKeywords(int facultyId) {
        List<String> keywords = new ArrayList<>();
        String sql = "SELECT keyword FROM faculty_keywords WHERE faculty_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, facultyId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) keywords.add(rs.getString("keyword"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return keywords;
    }

    public List<String> searchStudentsByKeyword(String keyword) {
        List<String> results = new ArrayList<>();
        String sql = "SELECT DISTINCT s.first_name, s.last_name, s.email " +
                     "FROM students s JOIN student_keywords sk ON s.student_id = sk.student_id " +
                     "WHERE sk.keyword LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(rs.getString("first_name") + " " + rs.getString("last_name") +
                            " - " + rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public Faculty getFacultyById(int facultyId) {
        String sql = "SELECT * FROM faculty WHERE faculty_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, facultyId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Faculty(facultyId,
                    rs.getString("first_name"), rs.getString("last_name"),
                    rs.getString("building"), rs.getString("office_number"),
                    rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Searches for students whose keywords match the faculty's keywords or abstracts.
     * Per requirement: "compare faculty interest keywords with student keywords and/or
     * compare the faculty's abstracts stored in the database to the student's keywords of interest"
     */
    public List<String> searchStudentsByFacultyMatch(int facultyId) {
        List<String> results = new ArrayList<>();
        // Find students whose keywords match faculty's keywords OR appear in faculty's abstracts
        String sql = "SELECT DISTINCT s.student_id, s.first_name, s.last_name, s.email " +
                     "FROM students s " +
                     "JOIN student_keywords sk ON s.student_id = sk.student_id " +
                     "WHERE sk.keyword IN (" +
                     "    SELECT keyword FROM faculty_keywords WHERE faculty_id = ?" +
                     ") OR EXISTS (" +
                     "    SELECT 1 FROM faculty_abstracts fa " +
                     "    WHERE fa.faculty_id = ? AND fa.abstract_text LIKE CONCAT('%', sk.keyword, '%')" +
                     ")";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, facultyId);
            pstmt.setInt(2, facultyId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(rs.getString("first_name") + " " + rs.getString("last_name") +
                            " - " + rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Gets all abstracts for a faculty member.
     */
    public List<String> getFacultyAbstracts(int facultyId) {
        List<String> abstracts = new ArrayList<>();
        String sql = "SELECT abstract_id, abstract_type, abstract_text FROM faculty_abstracts WHERE faculty_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, facultyId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                abstracts.add("[" + rs.getInt("abstract_id") + "] " +
                            rs.getString("abstract_type") + ": " +
                            rs.getString("abstract_text"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return abstracts;
    }
}
