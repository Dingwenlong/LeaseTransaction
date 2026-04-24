@echo off
chcp 65001 >nul
cd /d "%~dp0"

set "BACKEND_PORT=8081"

echo.
echo ==========================================
echo   Mini Program Dev Launcher
echo ==========================================
echo.

powershell -NoProfile -ExecutionPolicy Bypass -Command "$client = $null; try { $client = New-Object Net.Sockets.TcpClient; $client.Connect('127.0.0.1', %BACKEND_PORT%); exit 0 } catch { exit 1 } finally { if ($client) { $client.Close() } }" >nul 2>&1
if errorlevel 1 (
  echo [INFO] Backend is not running. Starting local dependencies and backend...
  powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0start-dev.ps1"
) else (
  echo [OK] Backend is already running.
)

echo Opening mini program folder...
start "" explorer.exe "%~dp0miniprogram"
echo Backend API: http://127.0.0.1:%BACKEND_PORT%
