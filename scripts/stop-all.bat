@echo off
chcp 936 >nul
echo ========================================
echo   Campus Lease Trading System - Shutdown
echo ========================================
echo.

set "SCRIPT_DIR=%~dp0"
set "PROJECT_DIR=%SCRIPT_DIR%.."

echo Stopping Docker services...
cd /d "%PROJECT_DIR%"
docker-compose down

echo.
echo All Docker services have been stopped.
echo.
echo Note: Backend and Frontend terminal windows need to be closed manually.
echo.
pause
