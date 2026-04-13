@echo off
REM Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
REM Date: 2026-04-13 | ISTE 330
REM Compile and run the Faculty Research Database application

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

REM Compile
echo Compiling...
javac -cp .;"%JAR_FILE%" ^
    src\Main.java ^
    src\dao\*.java ^
    src\model\*.java ^
    src\gui\*.java ^
    src\test\*.java ^
    -d out

REM Copy db.properties
copy db.properties out\ >nul

echo Running application...
java -cp out;"%JAR_FILE%" Main
