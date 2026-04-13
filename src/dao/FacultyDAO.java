// Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
// Date: 2026-04-13 | ISTE 330

package dao;

import model.Faculty;
import model.Student;
import exception.DatabaseException;
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
            throw new DatabaseException("insertAbstract", 
                "Failed to insert abstract for faculty ID " + facultyId, e);
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
            throw new DatabaseException("updateAbstract",
                "Failed to update abstract ID " + abstractId, e);
        }
    }

    public boolean deleteAbstract(int abstractId) {
        String sql = "DELETE FROM faculty_abstracts WHERE abstract_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, abstractId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("deleteAbstract",
                "Failed to delete abstract ID " + abstractId, e);
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
            throw new DatabaseException("getFacultyKeywords",
                "Failed to get keywords for faculty ID " + facultyId, e);
        }
        return keywords;
    }

    /**
     * Searches students by keyword, returning Student model objects.
     */
    public List<Student> searchStudentsByKeywordModels(String keyword) {
        List<Student> results = new ArrayList<>();
        String sql = "SELECT DISTINCT s.student_id, s.first_name, s.last_name, s.email " +
                     "FROM students s JOIN student_keywords sk ON s.student_id = sk.student_id " +
                     "WHERE sk.keyword LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(new Student(
                    rs.getInt("student_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException("searchStudentsByKeywordModels",
                "Failed to search students by keyword: " + keyword, e);
        }
        return results;
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
            throw new DatabaseException("searchStudentsByKeyword",
                "Failed to search students by keyword: " + keyword, e);
        }
        return results;
    }

    public Faculty getFacultyById(int facultyId) {
        String sql = "SELECT faculty_id, first_name, last_name, building, office_number, email FROM faculty WHERE faculty_id = ?";
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
            throw new DatabaseException("getFacultyById",
                "Failed to get faculty by ID " + facultyId, e);
        }
        return null;
    }

    /**
     * Searches for students whose keywords match the faculty's keywords or abstracts.
     * Returns Student model objects.
     */
    public List<Student> searchStudentsByFacultyMatchModels(int facultyId) {
        List<Student> results = new ArrayList<>();
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
                results.add(new Student(
                    rs.getInt("student_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException("searchStudentsByFacultyMatchModels",
                "Failed to match students for faculty ID " + facultyId, e);
        }
        return results;
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
            throw new DatabaseException("searchStudentsByFacultyMatch",
                "Failed to match students for faculty ID " + facultyId, e);
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
            throw new DatabaseException("getFacultyAbstracts",
                "Failed to get abstracts for faculty ID " + facultyId, e);
        }
        return abstracts;
    }
}
