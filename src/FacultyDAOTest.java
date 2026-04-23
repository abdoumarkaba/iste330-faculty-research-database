// Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
// Date: 2026-04-13 | ISTE 330

import java.util.List;

/**
 * Test class for FacultyDAO.
 * Tests all CRUD operations and search functionality.
 */
public class FacultyDAOTest {

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("FacultyDAO Test");
        System.out.println("====================================\n");

        FacultyDAO dao = new FacultyDAO();
        int passed = 0;
        int failed = 0;

        // Test 1: getFacultyById - Valid ID (queries first available faculty)
        System.out.println("Test 1: getFacultyById - Retrieve first available faculty");
        Faculty f = dao.getFacultyById(1);
        if (f != null && f.getFirstName() != null && f.getLastName() != null) {
            System.out.println("  PASSED: Retrieved faculty: " + f.getFirstName() + " " + f.getLastName());
            passed++;
        } else {
            System.out.println("  FAILED: Could not retrieve faculty with ID 1 (ensure schema has sample data)");
            failed++;
        }

        // Test 2: getFacultyById - Invalid ID
        System.out.println("\nTest 2: getFacultyById(999)");
        Faculty fInvalid = dao.getFacultyById(999);
        if (fInvalid == null) {
            System.out.println("  PASSED: Returns null for invalid ID");
            passed++;
        } else {
            System.out.println("  FAILED: Should return null for invalid ID");
            failed++;
        }

        // Test 3: getFacultyKeywords
        System.out.println("\nTest 3: getFacultyKeywords - Retrieve keywords for faculty 1");
        List<String> keywords = dao.getFacultyKeywords(1);
        if (!keywords.isEmpty()) {
            System.out.println("  PASSED: Keywords found: " + String.join(", ", keywords));
            passed++;
        } else {
            System.out.println("  FAILED: No keywords found for faculty ID 1 (ensure schema has sample data)");
            failed++;
        }

        // Test 4: getFacultyAbstracts
        System.out.println("\nTest 4: getFacultyAbstracts(1)");
        List<String> abstracts = dao.getFacultyAbstracts(1);
        if (!abstracts.isEmpty()) {
            System.out.println("  PASSED: Abstracts found: " + abstracts.size());
            passed++;
        } else {
            System.out.println("  FAILED: No abstracts found");
            failed++;
        }

        // Test 5: searchStudentsByKeyword
        System.out.println("\nTest 5: searchStudentsByKeyword - Search for students by keyword");
        List<String> students = dao.searchStudentsByKeyword("AI");
        if (!students.isEmpty()) {
            System.out.println("  PASSED: Found students: " + students);
            passed++;
        } else {
            System.out.println("  FAILED: No students found for keyword 'AI' (ensure schema has sample data)");
            failed++;
        }

        // Test 6: searchStudentsByFacultyMatch
        System.out.println("\nTest 6: searchStudentsByFacultyMatch(1)");
        List<String> matchedStudents = dao.searchStudentsByFacultyMatch(1);
        if (!matchedStudents.isEmpty()) {
            System.out.println("  PASSED: Found matching students: " + matchedStudents);
            passed++;
        } else {
            System.out.println("  FAILED: No matching students found for faculty 1");
            failed++;
        }

        // Test 7: insertAbstract (requires cleanup)
        System.out.println("\nTest 7: insertAbstract(1, 'book', 'Test abstract')");
        boolean inserted = dao.insertAbstract(1, "book", "Test abstract for testing");
        if (inserted) {
            System.out.println("  PASSED: Abstract inserted successfully");
            passed++;
        } else {
            System.out.println("  FAILED: Could not insert abstract");
            failed++;
        }

        // Test 8: updateAbstract (update the one we just inserted)
        System.out.println("\nTest 8: updateAbstract");
        // First get the abstract ID we just created
        List<String> abs = dao.getFacultyAbstracts(1);
        int abstractId = -1;
        for (String a : abs) {
            if (a.contains("Test abstract for testing")) {
                String idStr = a.substring(1, a.indexOf("]"));
                abstractId = Integer.parseInt(idStr);
                break;
            }
        }
        if (abstractId > 0) {
            boolean updated = dao.updateAbstract(abstractId, "Updated test abstract");
            if (updated) {
                System.out.println("  PASSED: Abstract updated successfully");
                passed++;
            } else {
                System.out.println("  FAILED: Could not update abstract");
                failed++;
            }
        } else {
            System.out.println("  SKIPPED: Could not find inserted abstract to update");
        }

        // Test 9: deleteAbstract
        System.out.println("\nTest 9: deleteAbstract");
        if (abstractId > 0) {
            boolean deleted = dao.deleteAbstract(abstractId);
            if (deleted) {
                System.out.println("  PASSED: Abstract deleted successfully");
                passed++;
            } else {
                System.out.println("  FAILED: Could not delete abstract");
                failed++;
            }
        } else {
            System.out.println("  SKIPPED: Could not find inserted abstract to delete");
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
