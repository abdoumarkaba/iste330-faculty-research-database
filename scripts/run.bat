@echo off
REM Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
REM Date: 2026-04-13 | ISTE 330
REM Compile and run the Faculty Research Database application (Local MySQL)

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

REM Compile
echo Compiling...
javac -cp .;"%JAR_FILE%" src\*.java -d out

REM Copy db.properties
copy src\main\resources\db.properties out\ >nul

echo Running application...
java -cp out;"%JAR_FILE%" Main
