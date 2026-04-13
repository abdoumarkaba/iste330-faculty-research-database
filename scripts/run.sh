#!/bin/bash
# Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
# Date: 2026-04-13 | ISTE 330
# Compile and run the Faculty Research Database application

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

# Compile
echo "Compiling..."
javac -cp .:"$JAR_FILE" \
    src/Main.java \
    src/dao/*.java \
    src/model/*.java \
    src/gui/*.java \
    src/test/*.java \
    -d out

# Copy db.properties
cp db.properties out/

echo "Running application..."
java -cp out:"$JAR_FILE" Main
