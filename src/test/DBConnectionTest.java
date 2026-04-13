// Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
// Date: 2026-04-13 | ISTE 330

package test;

import dao.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Test class for DBConnection.
 * Verifies database connectivity is working correctly.
 */
public class DBConnectionTest {

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("DBConnection Test");
        System.out.println("====================================\n");

        int passed = 0;
        int failed = 0;

        // Test 1: Get Connection
        System.out.println("Test 1: getConnection()");
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("  PASSED: Connection established successfully");
                passed++;
            } else {
                System.out.println("  FAILED: Connection is null or closed");
                failed++;
            }
        } catch (SQLException e) {
            System.out.println("  FAILED: " + e.getMessage());
            failed++;
        }

        // Test 2: Connection is valid
        System.out.println("\nTest 2: Connection validity");
        try (Connection conn = DBConnection.getConnection()) {
            if (conn.isValid(5)) {
                System.out.println("  PASSED: Connection is valid");
                passed++;
            } else {
                System.out.println("  FAILED: Connection is not valid");
                failed++;
            }
        } catch (SQLException e) {
            System.out.println("  FAILED: " + e.getMessage());
            failed++;
        }

        // Test 3: Catalog is correct
        System.out.println("\nTest 3: Database catalog");
        try (Connection conn = DBConnection.getConnection()) {
            String catalog = conn.getCatalog();
            if ("faculty_research_db".equals(catalog)) {
                System.out.println("  PASSED: Connected to correct database: " + catalog);
                passed++;
            } else {
                System.out.println("  FAILED: Wrong database: " + catalog);
                failed++;
            }
        } catch (SQLException e) {
            System.out.println("  FAILED: " + e.getMessage());
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
