@echo off
REM Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
REM Date: 2026-04-13 | ISTE 330
REM Run all backend tests for the Faculty Research Database application

setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set PROJECT_DIR=%SCRIPT_DIR%..
set MYSQL_JAR=%PROJECT_DIR%\mysql-connector-java-8.0.21.jar
set HIKARI_JAR=%PROJECT_DIR%\HikariCP-5.0.1.jar
set JAR_FILES=%MYSQL_JAR%;%HIKARI_JAR%

REM Check if MySQL connector exists
if not exist "%MYSQL_JAR%" (
    echo Error: MySQL connector JAR not found at %MYSQL_JAR%
    echo Download from: https://dev.mysql.com/downloads/connector/j/
    exit /b 1
)

REM Check if HikariCP exists
if not exist "%HIKARI_JAR%" (
    echo Error: HikariCP JAR not found at %HIKARI_JAR%
    echo Download from: https://search.maven.org/artifact/com.zaxxer/HikariCP/5.0.1/jar
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
    javac -cp .;"%JAR_FILES%" ^
        src\dao\*.java ^
        src\model\*.java ^
        src\exception\*.java ^
        src\util\*.java ^
        src\test\*.java ^
        -d out
    copy db.properties out\ >nul
    echo. >nul
)

echo ============================================
echo Running Backend Tests
echo ============================================

set TOTAL_PASSED=0
set TOTAL_FAILED=0

for %%t in (DBConnectionTest FacultyDAOTest StudentDAOTest PublicUserDAOTest SecurityTest ConnectionPoolTest) do (
    echo.
    echo --- Running %%t ---
    java -cp out;"%JAR_FILES%" test.%%t
    
    REM Note: Counting passed/failed requires parsing output
    REM Tests exit with code 1 if any failed
)

echo.
echo ============================================
echo Test run complete
echo ============================================
