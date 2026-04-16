@echo off
REM Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
REM Date: 2026-04-13 | ISTE 330
REM Compile and run the Faculty Research Database application

setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set PROJECT_DIR=%SCRIPT_DIR%..
set LIB_CP=%PROJECT_DIR%\*

cd /d "%PROJECT_DIR%"

REM Ensure database is running
docker compose ps db --status "running" 2>nul | findstr "running" >nul
if errorlevel 1 (
    echo Starting database...
    docker compose up -d
    echo Waiting for database to be ready...
    timeout /t 10 /nobreak >nul
)

REM Clean old build
if exist out rmdir /s /q out
mkdir out

REM Compile
echo Compiling...
javac -cp ".;%LIB_CP%" -sourcepath src\main\java -d out src\main\java\Main.java

if errorlevel 1 (
    echo Compile failed.
    pause
    exit /b 1
)

REM Copy config
copy db.properties out\ >nul

echo Running application...
java -cp "out;%LIB_CP%" Main

pause