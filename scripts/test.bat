@echo off
REM Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
REM Date: 2026-04-13 | ISTE 330
REM Run all backend tests for the Faculty Research Database application

setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set PROJECT_DIR=%SCRIPT_DIR%..
set JAR_FILE=%PROJECT_DIR%\mysql-connector-java-8.0.21.jar

REM Check if MySQL connector exists
if not exist "%JAR_FILE%" (
    echo Error: MySQL connector JAR not found at %JAR_FILE%
    echo Download from: https://dev.mysql.com/downloads/connector/j/
    exit /b 1
)

cd /d "%PROJECT_DIR%"

REM Ensure database is running
docker compose ps db --status "running" 2>nul | findstr "running" >nul
if errorlevel 1 (
    echo Starting database...
    docker compose up -d
    echo Waiting for database to be ready...
    timeout /t 10 /nobreak >nul
)

REM Compile if needed
if not exist "out\test\DBConnectionTest.class" (
    echo Compiling...
    javac -cp .;"%JAR_FILE%" ^
        src\dao\*.java ^
        src\model\*.java ^
        src\test\*.java ^
        -d out
    copy db.properties out\ >nul
)

echo ============================================
echo Running Backend Tests
echo ============================================

set TOTAL_PASSED=0
set TOTAL_FAILED=0

for %%t in (DBConnectionTest FacultyDAOTest StudentDAOTest PublicUserDAOTest) do (
    echo.
    echo --- Running %%t ---
    java -cp out;"%JAR_FILE%" test.%%t
    
    REM Note: Counting passed/failed requires parsing output
    REM Tests exit with code 1 if any failed
)

echo.
echo ============================================
echo Test run complete
echo ============================================
