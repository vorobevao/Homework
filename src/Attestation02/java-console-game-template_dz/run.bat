@echo off
chcp 65001 > nul
set JAVA_OPTS=-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8
set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8

echo ========================================
echo    DUNGEON MINI GAME 
echo ========================================
echo.

java -cp "bin" %JAVA_OPTS% com.example.dungeon.core.Game

echo.
echo ========================================
echo    GAME FINISHED
echo ========================================
echo.
pause