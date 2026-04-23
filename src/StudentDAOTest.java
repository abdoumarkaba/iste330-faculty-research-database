// Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
// Date: 2026-04-13 | ISTE 330

import java.util.List;

/**
 * Test class for StudentDAO.
 * Tests search functionality and student retrieval.
 */
public class StudentDAOTest {

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("StudentDAO Test");
        System.out.println("====================================\n");

        StudentDAO dao = new StudentDAO();
        int passed = 0;
        int failed = 0;

        // Test 1: isValidStudentId - Valid
        System.out.println("Test 1: isValidStudentId(1)");
        if (dao.isValidStudentId(1)) {
            System.out.println("  PASSED: Student ID 1 is valid");
            passed++;
        } else {
            System.out.println("  FAILED: Student ID 1 should be valid");
            failed++;
        }

        // Test 2: isValidStudentId - Invalid
        System.out.println("\nTest 2: isValidStudentId(999)");
        if (!dao.isValidStudentId(999)) {
            System.out.println("  PASSED: Invalid ID correctly rejected");
            passed++;
        } else {
            System.out.println("  FAILED: ID 999 should be invalid");
            failed++;
        }

        // Test 3: getStudentById
        System.out.println("\nTest 3: getStudentById - Retrieve first available student");
        Student s = dao.getStudentById(1);
        if (s != null && s.getFirstName() != null && s.getLastName() != null) {
            System.out.println("  PASSED: Retrieved student: " + s.getFirstName() + " " + s.getLastName());
            passed++;
        } else {
            System.out.println("  FAILED: Could not retrieve student with ID 1 (ensure schema has sample data)");
            failed++;
        }

        // Test 4: getStudentKeywords
        System.out.println("\nTest 4: getStudentKeywords - Retrieve keywords for student 1");
        List<String> keywords = dao.getStudentKeywords(1);
        if (!keywords.isEmpty()) {
            System.out.println("  PASSED: Keywords found: " + String.join(", ", keywords));
            passed++;
        } else {
            System.out.println("  FAILED: No keywords found for student ID 1 (ensure schema has sample data)");
            failed++;
        }

        // Test 5: searchFacultyByKeyword - Keyword match
        System.out.println("\nTest 5: searchFacultyByKeyword - Search for faculty by keyword");
        List<String> faculty = dao.searchFacultyByKeyword("AI");
        if (!faculty.isEmpty()) {
            System.out.println("  PASSED: Found faculty: " + faculty);
            passed++;
        } else {
            System.out.println("  FAILED: No faculty found for keyword 'AI' (ensure schema has sample data)");
            failed++;
        }

        // Test 6: searchFacultyByKeyword - Abstract match
        System.out.println("\nTest 6: searchFacultyByKeyword(\"cybersecurity\")");
        List<String> facultyByAbstract = dao.searchFacultyByKeyword("cybersecurity");
        if (!facultyByAbstract.isEmpty()) {
            System.out.println("  PASSED: Found faculty via abstract: " + facultyByAbstract);
            passed++;
        } else {
            System.out.println("  FAILED: No faculty found via abstract for 'cybersecurity'");
            failed++;
        }

        // Test 7: getAllFaculty
        System.out.println("\nTest 7: getAllFaculty()");
        List<String> allFaculty = dao.getAllFaculty();
        if (allFaculty.size() >= 3) {
            System.out.println("  PASSED: Found " + allFaculty.size() + " faculty members");
            passed++;
        } else {
            System.out.println("  FAILED: Expected at least 3 faculty, found " + allFaculty.size());
            failed++;
        }

        // Summary
        System.out.println("\n====================================");
        System.out.println("SUMMARY: " + passed + " passed, " + failed + " failed");
        System.out.println("====================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
