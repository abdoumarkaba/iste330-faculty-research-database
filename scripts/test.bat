@echo off
REM Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
REM Date: 2026-04-13 | ISTE 330
REM Run all backend tests for the Faculty Research Database application (Local MySQL)

setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set PROJECT_DIR=%SCRIPT_DIR%..
set MYSQL_JAR=%PROJECT_DIR%\mysql-connector-java-8.0.21.jar

REM Check if MySQL connector exists
if not exist "%MYSQL_JAR%" (
    echo Error: MySQL connector JAR not found at %MYSQL_JAR%
    echo Download from: https://dev.mysql.com/downloads/connector/j/
    exit /b 1
)

cd /d "%PROJECT_DIR%"

REM Compile if needed
if not exist "out\DBConnectionTest.class" (
    echo Compiling...
    javac -cp .;"%MYSQL_JAR%" src\*.java -d out
    copy src\main\resources\db.properties out\ >nul
)

echo ============================================
echo Running Backend Tests
echo ============================================

set TOTAL_PASSED=0
set TOTAL_FAILED=0

for %%t in (DBConnectionTest FacultyDAOTest StudentDAOTest PublicUserDAOTest SecurityTest) do (
    echo.
    echo --- Running %%t ---
    java -cp out;"%MYSQL_JAR%" %%t
)

echo.
echo ============================================
echo Test run complete
echo ============================================
