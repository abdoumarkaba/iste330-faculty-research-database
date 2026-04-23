#!/bin/bash
# Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
# Date: 2026-04-13 | ISTE 330
# Compile and run the Faculty Research Database application (Local MySQL)

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

# Compile
echo "Compiling..."
javac -cp .:"$JAR_FILE" src/*.java -d out

# Copy db.properties
cp src/main/resources/db.properties out/

echo "Running application..."
java -cp out:"$JAR_FILE" Main
