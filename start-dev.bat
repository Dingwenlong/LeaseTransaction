@echo off
chcp 65001 >nul
cd /d "%~dp0"

title Campus Lease Transaction - Dev Launcher

echo.
echo ==========================================
echo   Campus Lease Transaction Dev Launcher
echo ==========================================
echo.

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0start-dev.ps1"
pause
