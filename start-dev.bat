@echo off
chcp 65001 >nul
cd /d "%~dp0"

title Campus Lease Transaction - Dev Launcher

echo.
echo ==========================================
echo   Campus Lease Transaction Dev Launcher
echo ==========================================
echo.

echo [1/5] Checking Docker services...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$containers = docker ps --filter 'name=campus-errand-mysql' --format '{{.Names}}' 2>$null; if ($containers) { exit 0 } else { $containers = docker ps --filter 'name=lease-mysql' --format '{{.Names}}' 2>$null; if ($containers) { exit 0 } else { exit 1 } }"
if %errorlevel% equ 0 (
    echo [OK] Docker MySQL is running.
) else (
    echo [ERROR] No Docker MySQL found. Please start campus-errand-mysql first.
    pause
    exit /b 1
)

echo [2/5] Waiting for MySQL on 127.0.0.1:3306...
call :wait_for_port 3306 60 "MySQL"
if errorlevel 1 (
    pause
    exit /b 1
)

echo [3/5] Waiting for Redis on 127.0.0.1:6379...
call :wait_for_port 6379 60 "Redis"
if errorlevel 1 (
    pause
    exit /b 1
)

set "LEASE_DB_PORT=3306"
set "LEASE_DB_USERNAME=root"
set "LEASE_DB_PASSWORD=root_password"
set "LEASE_REDIS_PORT=6379"
set "LEASE_REDIS_HOST=127.0.0.1"

echo [4/5] Starting backend service...
start "Lease Backend" cmd /k "cd /d "%~dp0backend\lease-backend" && powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0run-backend.ps1""

echo [5/5] Starting admin console...
if not exist "%~dp0admin\lease-admin\node_modules" (
    echo [lease-admin] Installing npm dependencies...
    cd /d "%~dp0admin\lease-admin"
    call npm install
    cd /d "%~dp0"
)
start "Lease Admin" cmd /k "cd /d "%~dp0admin\lease-admin" && npm run dev"

echo Opening miniprogram folder...
start explorer.exe "%~dp0miniprogram"

echo.
echo ==========================================
echo   Services are being launched:
echo   Backend: http://127.0.0.1:8081
echo   Admin:   http://127.0.0.1:5173
echo   MiniApp: Open miniprogram in WeChat DevTools
echo ==========================================
echo.
pause
exit /b 0

:wait_for_port
set "TARGET_PORT=%~1"
set "WAIT_SECONDS=%~2"
set "SERVICE_NAME=%~3"
set /a ELAPSED=0

:wait_loop
powershell -NoProfile -ExecutionPolicy Bypass -Command "$client = $null; try { $client = New-Object Net.Sockets.TcpClient; $client.Connect('127.0.0.1', %TARGET_PORT%); exit 0 } catch { exit 1 } finally { if ($client) { $client.Close() } }" >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] %SERVICE_NAME% is ready.
    exit /b 0
)

if %ELAPSED% GEQ %WAIT_SECONDS% (
    echo [ERROR] Timeout waiting for %SERVICE_NAME% on port %TARGET_PORT%.
    exit /b 1
)

timeout /t 2 /nobreak >nul
set /a ELAPSED+=2
goto :wait_loop
