// Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
// Date: 2026-04-13 | ISTE 330

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Faculty operations.
 * All public methods validate input parameters to prevent SQL injection and ensure data integrity.
 */
public class FacultyDAO {

    /**
     * Inserts a new abstract for a faculty member.
     * Validates all inputs to prevent SQL injection and ensure data integrity.
     */
    public boolean insertAbstract(int facultyId, String abstractType, String abstractText) {
        // Security: Validate all inputs
        ValidationUtil.validateFacultyId(facultyId);
        ValidationUtil.validateAbstractType(abstractType);
        ValidationUtil.validateAbstractText(abstractText);
        
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

    /**
     * Updates an existing abstract.
     * Validates inputs to prevent SQL injection.
     */
    public boolean updateAbstract(int abstractId, String abstractText) {
        // Security: Validate inputs
        ValidationUtil.validateAbstractId(abstractId);
        ValidationUtil.validateAbstractText(abstractText);
        
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

    /**
     * Deletes an abstract by ID.
     * Validates the abstract ID to prevent invalid operations.
     */
    public boolean deleteAbstract(int abstractId) {
        // Security: Validate input
        ValidationUtil.validateAbstractId(abstractId);
        
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

    /**
     * Gets all keywords for a faculty member.
     * Validates the faculty ID before querying.
     */
    public List<String> getFacultyKeywords(int facultyId) {
        // Security: Validate input
        ValidationUtil.validateFacultyId(facultyId);
        
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
     * Inserts a new keyword for a faculty member.
     * Validates all inputs to prevent SQL injection and ensure data integrity.
     */
    public boolean insertKeyword(int facultyId, String keyword) {
        // Security: Validate all inputs
        ValidationUtil.validateFacultyId(facultyId);
        ValidationUtil.validateKeyword(keyword);
        
        String sql = "INSERT INTO faculty_keywords (faculty_id, keyword) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, facultyId);
            pstmt.setString(2, keyword);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new DatabaseException("insertKeyword", 
                "Failed to insert keyword for faculty ID " + facultyId, e);
        }
    }

    /**
     * Updates an existing keyword for a faculty member.
     * Validates inputs to prevent SQL injection.
     */
    public boolean updateKeyword(int keywordId, String newKeyword) {
        // Security: Validate inputs
        ValidationUtil.validateKeywordId(keywordId);
        ValidationUtil.validateKeyword(newKeyword);
        
        String sql = "UPDATE faculty_keywords SET keyword = ? WHERE keyword_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newKeyword);
            pstmt.setInt(2, keywordId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("updateKeyword",
                "Failed to update keyword ID " + keywordId, e);
        }
    }

    /**
     * Deletes a keyword by ID.
     * Validates the keyword ID to prevent invalid operations.
     */
    public boolean deleteKeyword(int keywordId) {
        // Security: Validate input
        ValidationUtil.validateKeywordId(keywordId);
        
        String sql = "DELETE FROM faculty_keywords WHERE keyword_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, keywordId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("deleteKeyword",
                "Failed to delete keyword ID " + keywordId, e);
        }
    }

    /**
     * Searches students by keyword, returning Student model objects.
     * Validates the search keyword to prevent SQL injection.
     */
    public List<Student> searchStudentsByKeywordModels(String keyword) {
        // Security: Validate search keyword
        ValidationUtil.validateSearchKeyword(keyword);
        
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

    /**
     * Searches students by keyword, returning formatted strings.
     * Validates the search keyword to prevent SQL injection.
     */
    public List<String> searchStudentsByKeyword(String keyword) {
        // Security: Validate search keyword
        ValidationUtil.validateSearchKeyword(keyword);
        
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

    /**
     * Gets a faculty member by ID.
     * Validates the faculty ID before querying.
     */
    public Faculty getFacultyById(int facultyId) {
        // Security: Validate input
        ValidationUtil.validateFacultyId(facultyId);
        
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
     * Validates the faculty ID before querying.
     */
    public List<Student> searchStudentsByFacultyMatchModels(int facultyId) {
        // Security: Validate input
        ValidationUtil.validateFacultyId(facultyId);
        
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
     * Validates the faculty ID before querying.
     */
    public List<String> searchStudentsByFacultyMatch(int facultyId) {
        // Security: Validate input
        ValidationUtil.validateFacultyId(facultyId);
        
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
     * Validates the faculty ID before querying.
     */
    public List<String> getFacultyAbstracts(int facultyId) {
        // Security: Validate input
        ValidationUtil.validateFacultyId(facultyId);
        
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
    
    // ==================== PASSWORD AUTHENTICATION METHODS ====================
    
    /**
     * Gets the stored password hash for a faculty member.
     * Returns NULL if faculty doesn't exist or has no password set.
     * 
     * @param facultyId the faculty ID
     * @return password hash string, or null if not found/no password
     */
    public String getPasswordHash(int facultyId) {
        ValidationUtil.validateFacultyId(facultyId);
        
        String sql = "SELECT password_hash FROM faculty WHERE faculty_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, facultyId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("password_hash");
            }
        } catch (SQLException e) {
            throw new DatabaseException("getPasswordHash",
                "Failed to get password hash for faculty ID " + facultyId, e);
        }
        return null;
    }
    
    /**
     * Sets or updates the password hash for a faculty member.
     * 
     * @param facultyId the faculty ID
     * @param passwordHash the hashed password string
     * @return true if successful
     */
    public boolean setPasswordHash(int facultyId, String passwordHash) {
        ValidationUtil.validateFacultyId(facultyId);
        
        if (passwordHash == null || passwordHash.isEmpty()) {
            throw new IllegalArgumentException("Password hash cannot be empty");
        }
        
        String sql = "UPDATE faculty SET password_hash = ? WHERE faculty_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, passwordHash);
            pstmt.setInt(2, facultyId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("setPasswordHash",
                "Failed to set password hash for faculty ID " + facultyId, e);
        }
    }
    
    /**
     * Authenticates a faculty member with ID and password.
     * 
     * @param facultyId the faculty ID
     * @param password the plain text password
     * @return true if authentication successful
     */
    public boolean authenticateFaculty(int facultyId, String password) {
        // Validate inputs
        ValidationUtil.validateFacultyId(facultyId);
        if (password == null || password.isEmpty()) {
            return false;
        }
        
        // Get stored password hash
        String storedHash = getPasswordHash(facultyId);
        
        // If no password set, authentication fails
        if (storedHash == null || storedHash.isEmpty()) {
            return false;
        }
        
        // Verify password
        return PasswordUtil.verifyPassword(password, storedHash);
    }
    
    /**
     * Checks if a faculty account is activated (has a password set).
     * 
     * @param facultyId the faculty ID
     * @return true if account is activated
     */
    public boolean isAccountActivated(int facultyId) {
        ValidationUtil.validateFacultyId(facultyId);
        
        String sql = "SELECT password_hash FROM faculty WHERE faculty_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, facultyId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String hash = rs.getString("password_hash");
                return hash != null && !hash.isEmpty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("isAccountActivated",
                "Failed to check account status for faculty ID " + facultyId, e);
        }
        return false;
    }
}
