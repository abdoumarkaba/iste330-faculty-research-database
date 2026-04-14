// Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
// Date: 2026-04-13 | ISTE 330

package dao;

import model.Student;
import model.Faculty;
import exception.DatabaseException;
import util.ValidationUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Student operations.
 * All public methods validate input parameters to prevent SQL injection and ensure data integrity.
 */
public class StudentDAO {

    /**
     * Searches faculty by keyword.
     * Validates the search keyword to prevent SQL injection.
     */
    public List<String> searchFacultyByKeyword(String keyword) {
        // Security: Validate search keyword
        ValidationUtil.validateSearchKeyword(keyword);
        
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
     * Validates the search keyword to prevent SQL injection.
     */
    public List<Faculty> searchFacultyByKeywordModels(String keyword) {
        // Security: Validate search keyword
        ValidationUtil.validateSearchKeyword(keyword);
        
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

    /**
     * Validates if a student ID exists in the database.
     * Validates the ID format before querying.
     */
    public boolean isValidStudentId(int studentId) {
        // Security: Validate student ID
        ValidationUtil.validateStudentId(studentId);
        
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

    /**
     * Gets all keywords for a student.
     * Validates the student ID before querying.
     */
    public List<String> getStudentKeywords(int studentId) {
        // Security: Validate student ID
        ValidationUtil.validateStudentId(studentId);
        
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

    /**
     * Gets a student by ID.
     * Validates the student ID before querying.
     */
    public Student getStudentById(int studentId) {
        // Security: Validate student ID
        ValidationUtil.validateStudentId(studentId);
        
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
    
    // ==================== PASSWORD AUTHENTICATION METHODS ====================
    
    /**
     * Gets the stored password hash for a student.
     * Returns NULL if student doesn't exist or has no password set.
     * 
     * @param studentId the student ID
     * @return password hash string, or null if not found/no password
     */
    public String getPasswordHash(int studentId) {
        ValidationUtil.validateStudentId(studentId);
        
        String sql = "SELECT password_hash FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("password_hash");
            }
        } catch (SQLException e) {
            throw new DatabaseException("getPasswordHash",
                "Failed to get password hash for student ID " + studentId, e);
        }
        return null;
    }
    
    /**
     * Sets or updates the password hash for a student.
     * 
     * @param studentId the student ID
     * @param passwordHash the hashed password string
     * @return true if successful
     */
    public boolean setPasswordHash(int studentId, String passwordHash) {
        ValidationUtil.validateStudentId(studentId);
        
        if (passwordHash == null || passwordHash.isEmpty()) {
            throw new IllegalArgumentException("Password hash cannot be empty");
        }
        
        String sql = "UPDATE students SET password_hash = ? WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, passwordHash);
            pstmt.setInt(2, studentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("setPasswordHash",
                "Failed to set password hash for student ID " + studentId, e);
        }
    }
    
    /**
     * Authenticates a student with ID and password.
     * 
     * @param studentId the student ID
     * @param password the plain text password
     * @return true if authentication successful
     */
    public boolean authenticateStudent(int studentId, String password) {
        // Validate inputs
        ValidationUtil.validateStudentId(studentId);
        if (password == null || password.isEmpty()) {
            return false;
        }
        
        // Get stored password hash
        String storedHash = getPasswordHash(studentId);
        
        // If no password set, authentication fails
        if (storedHash == null || storedHash.isEmpty()) {
            return false;
        }
        
        // Verify password
        return util.PasswordUtil.verifyPassword(password, storedHash);
    }
    
    /**
     * Checks if a student account is activated (has a password set).
     * 
     * @param studentId the student ID
     * @return true if account is activated
     */
    public boolean isAccountActivated(int studentId) {
        ValidationUtil.validateStudentId(studentId);
        
        String sql = "SELECT password_hash FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String hash = rs.getString("password_hash");
                return hash != null && !hash.isEmpty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("isAccountActivated",
                "Failed to check account status for student ID " + studentId, e);
        }
        return false;
    }
}
