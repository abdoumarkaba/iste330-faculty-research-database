// Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
// Date: 2026-04-13 | ISTE 330

import java.util.List;

/**
 * Test class for PublicUserDAO.
 * Tests public search functionality.
 */
public class PublicUserDAOTest {

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("PublicUserDAO Test");
        System.out.println("====================================\n");

        PublicUserDAO dao = new PublicUserDAO();
        int passed = 0;
        int failed = 0;

        // Test 1: searchByKeyword - Find faculty
        System.out.println("Test 1: searchByKeyword(\"AI\")");
        List<String> results = dao.searchByKeyword("AI");
        if (!results.isEmpty()) {
            System.out.println("  PASSED: Found results: " + results);
            passed++;
        } else {
            System.out.println("  FAILED: No results found for 'AI'");
            failed++;
        }

        // Test 2: searchByKeyword - Find students
        System.out.println("\nTest 2: searchByKeyword(\"Security\")");
        List<String> securityResults = dao.searchByKeyword("Security");
        boolean hasStudent = false;
        boolean hasFaculty = false;
        for (String r : securityResults) {
            if (r.startsWith("student")) hasStudent = true;
            if (r.startsWith("faculty")) hasFaculty = true;
        }
        if (hasStudent || hasFaculty) {
            System.out.println("  PASSED: Found results: " + securityResults);
            passed++;
        } else {
            System.out.println("  FAILED: No results found for 'Security'");
            failed++;
        }

        // Test 3: searchByKeyword - No results
        System.out.println("\nTest 3: searchByKeyword(\"zzzzzzz\")");
        List<String> noResults = dao.searchByKeyword("zzzzzzz");
        if (noResults.isEmpty()) {
            System.out.println("  PASSED: No results for non-existent keyword");
            passed++;
        } else {
            System.out.println("  FAILED: Should not find results for 'zzzzzzz'");
            failed++;
        }

        // Test 4: getAllEntries
        System.out.println("\nTest 4: getAllEntries()");
        List<String> all = dao.getAllEntries();
        if (all.size() >= 6) { // 3 faculty + 3 students minimum
            System.out.println("  PASSED: Found " + all.size() + " entries");
            passed++;
        } else {
            System.out.println("  FAILED: Expected at least 6 entries, found " + all.size());
            failed++;
        }

        // Test 5: getAllEntries contains both types
        System.out.println("\nTest 5: getAllEntries() contains both faculty and students");
        List<String> allEntries = dao.getAllEntries();
        boolean hasFacultyType = false;
        boolean hasStudentType = false;
        for (String entry : allEntries) {
            if (entry.startsWith("faculty")) hasFacultyType = true;
            if (entry.startsWith("student")) hasStudentType = true;
        }
        if (hasFacultyType && hasStudentType) {
            System.out.println("  PASSED: Contains both faculty and student entries");
            passed++;
        } else {
            System.out.println("  FAILED: Missing faculty or student entries");
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
