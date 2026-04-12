@echo off
setlocal EnableExtensions EnableDelayedExpansion

set "ROOT_DIR=%~dp0"
if "%ROOT_DIR:~-1%"=="\" set "ROOT_DIR=%ROOT_DIR:~0,-1%"

echo.
echo ==========================================
echo   Mini Program Dev Launcher
echo ==========================================
echo.

set "BACKEND_PORT=8081"
set "BACKEND_URL=http://127.0.0.1:%BACKEND_PORT%"

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$client = $null; try { $client = New-Object Net.Sockets.TcpClient; $client.Connect('127.0.0.1', %BACKEND_PORT%); exit 0 } catch { exit 1 } finally { if ($client) { $client.Close() } }" >nul 2>&1
if errorlevel 1 (
  echo [WARNING] Backend is not running on port %BACKEND_PORT%.
  echo Starting backend service...
  echo.
  echo [IMPORTANT] Before running, ensure MySQL and Redis are available.
  echo   - Docker container: campus-errand-mysql (MySQL password: root_password)
  echo   - Docker container: campus-errand-redis
  echo.
  echo Checking Docker services...

  docker ps --filter "name=campus-errand-mysql" --format "{{.Names}}" | findstr "campus-errand-mysql" >nul
  if errorlevel 1 (
    echo [ERROR] campus-errand-mysql container is not running. Please start it first.
    exit /b 1
  )
  docker ps --filter "name=campus-errand-redis" --format "{{.Names}}" | findstr "campus-errand-redis" >nul
  if errorlevel 1 (
    echo [ERROR] campus-errand-redis container is not running. Please start it first.
    exit /b 1
  )
  echo [OK] Docker services are running.

  echo Creating lease_db database if not exists...
  docker exec campus-errand-mysql mysql -uroot -proot_password -e "CREATE DATABASE IF NOT EXISTS lease_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" >nul 2>&1
  type "%ROOT_DIR%\docker\mysql\init\01-schema.sql" | docker exec -i campus-errand-mysql mysql -uroot -proot_password lease_db >nul 2>&1
  echo [OK] Database initialized.

  echo Starting backend...
  start "Lease Backend" /D "%ROOT_DIR%\backend\lease-backend" cmd /k "powershell -NoProfile -ExecutionPolicy Bypass -File \"%ROOT_DIR%\\run-backend.ps1\""
  echo Waiting for backend to start...
  call :wait_for_backend
) else (
  echo [OK] Backend is already running.
)

echo.
echo Opening mini program folder...
start "" explorer.exe "%ROOT_DIR%\miniprogram"

echo.
echo ==========================================
echo   Mini Program Dev Environment Ready
echo ==========================================
echo.
echo Backend API: %BACKEND_URL%
echo MiniApp: Open the miniprogram folder in WeChat DevTools
echo.
echo Testing API connection...
call :test_api
echo.
exit /b 0

:wait_for_backend
set /a ELAPSED=0
:backend_wait_loop
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$client = $null; try { $client = New-Object Net.Sockets.TcpClient; $client.Connect('127.0.0.1', %BACKEND_PORT%); exit 0 } catch { exit 1 } finally { if ($client) { $client.Close() } }" >nul 2>&1
if not errorlevel 1 (
  echo [OK] Backend is ready.
  timeout /t 3 /nobreak >nul
  exit /b 0
)

if %ELAPSED% GEQ 120 (
  echo [ERROR] Timeout waiting for backend to start.
  exit /b 1
)

timeout /t 5 /nobreak >nul
set /a ELAPSED+=5
echo Still waiting... %ELAPSED%s
goto :backend_wait_loop

:test_api
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "try { $resp = Invoke-WebRequest -Uri '%BACKEND_URL%/api/test/hello' -Method GET -TimeoutSec 5 -UseBasicParsing; Write-Host '[API] Status:' $resp.StatusCode 'Response:' $resp.Content } catch { Write-Host '[API] Error:' $_.Exception.Message }"
exit /b 0
