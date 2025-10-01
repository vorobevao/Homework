@echo off
chcp 65001 > nul
echo ========================================
echo    BUILDING DUNGEON MINI GAME
echo ========================================
echo.

echo Checking Java installation...
java -version > nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH!
    echo Please install Java JDK 8 or higher.
    pause
    exit /b 1
)

echo.
echo Creating directory structure...
if not exist bin (
    echo Creating bin directory...
    mkdir bin
)

echo.
echo Compiling ALL source files...
javac -d bin src/com/example/dungeon/*.java src/com/example/dungeon/model/*.java src/com/example/dungeon/core/*.java

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo.
echo Checking compiled classes...
dir bin /s | find ".class"

echo.
echo ========================================
echo    BUILD SUCCESSFUL!
echo ========================================
echo.
echo You can now run the game with: run.bat
echo.

pause