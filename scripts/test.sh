#!/bin/bash
# Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
# Date: 2026-04-13 | ISTE 330
# Run all backend tests for the Faculty Research Database application

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
JAR_FILE="$PROJECT_DIR/mysql-connector-java-8.0.21.jar"

# Check if MySQL connector exists
if [ ! -f "$JAR_FILE" ]; then
    echo "Error: MySQL connector JAR not found at $JAR_FILE"
    echo "Download from: https://dev.mysql.com/downloads/connector/j/"
    exit 1
fi

cd "$PROJECT_DIR"

# Ensure database is running
if ! docker compose ps db --status "running" 2>/dev/null | grep -q "running"; then
    echo "Starting database..."
    docker compose up -d
    echo "Waiting for database to be ready..."
    sleep 10
fi

# Compile if needed
if [ ! -d "out" ] || [ ! -f "out/test/DBConnectionTest.class" ]; then
    echo "Compiling..."
    javac -cp .:"$JAR_FILE" \
        src/dao/*.java \
        src/model/*.java \
        src/test/*.java \
        -d out
    cp db.properties out/
fi

echo "============================================"
echo "Running Backend Tests"
echo "============================================"

TOTAL_PASSED=0
TOTAL_FAILED=0

for test_class in DBConnectionTest FacultyDAOTest StudentDAOTest PublicUserDAOTest; do
    echo ""
    echo "--- Running $test_class ---"
    java -cp out:"$JAR_FILE" "test.$test_class" 2>&1 | tee /tmp/test_output.txt
    
    # Count results
    passed=$(grep -c "PASSED" /tmp/test_output.txt 2>/dev/null || echo 0)
    failed=$(grep -c "FAILED" /tmp/test_output.txt 2>/dev/null || echo 0)
    TOTAL_PASSED=$((TOTAL_PASSED + passed))
    TOTAL_FAILED=$((TOTAL_FAILED + failed))
done

echo ""
echo "============================================"
echo "TOTAL: $TOTAL_PASSED passed, $TOTAL_FAILED failed"
echo "============================================"

if [ $TOTAL_FAILED -gt 0 ]; then
    exit 1
fi
