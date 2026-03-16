@echo off
setlocal EnableExtensions EnableDelayedExpansion

set "ROOT_DIR=%~dp0"
if "%ROOT_DIR:~-1%"=="\" set "ROOT_DIR=%ROOT_DIR:~0,-1%"
set "USE_DOCKER=1"

if /I "%~1"=="--docker" set "USE_DOCKER=1"
if /I "%~1"=="--local" set "USE_DOCKER=0"

if /I "%~1"=="-h" goto :help
if /I "%~1"=="--help" goto :help

echo.
echo ==========================================
echo   Campus Lease Transaction Dev Launcher
echo ==========================================
echo.

call :require_command java "Java"
if errorlevel 1 exit /b 1
call :require_command mvn "Maven"
if errorlevel 1 exit /b 1
call :require_command npm "npm"
if errorlevel 1 exit /b 1
if "%USE_DOCKER%"=="1" (
  echo [1/6] Checking docker port availability...
  call :ensure_port_free 3306 "MySQL"
  if errorlevel 1 exit /b 1
  call :ensure_port_free 6379 "Redis"
  if errorlevel 1 exit /b 1

  call :resolve_docker_compose
  if errorlevel 1 exit /b 1

  echo [2/6] Starting MySQL and Redis with Docker...
  pushd "%ROOT_DIR%"
  %DOCKER_COMPOSE_CMD% up -d
  if errorlevel 1 (
    popd
    echo [ERROR] Failed to start docker services.
    exit /b 1
  )
  popd

  echo [3/6] Waiting for MySQL on 127.0.0.1:3306...
  call :wait_for_port 3306 60 "MySQL" "mysql"
  if errorlevel 1 exit /b 1

  echo [4/6] Waiting for Redis on 127.0.0.1:6379...
  call :wait_for_port 6379 60 "Redis" "redis"
  if errorlevel 1 exit /b 1

  set "LEASE_DB_PORT=3306"
  if not defined LEASE_DB_USERNAME set "LEASE_DB_USERNAME=root"
  if not defined LEASE_DB_PASSWORD set "LEASE_DB_PASSWORD=root"
  if not defined LEASE_REDIS_PORT set "LEASE_REDIS_PORT=6379"
) else (
  if not defined LEASE_DB_PORT set "LEASE_DB_PORT=3307"
  if not defined LEASE_DB_USERNAME set "LEASE_DB_USERNAME=root"
  if not defined LEASE_DB_PASSWORD set "LEASE_DB_PASSWORD=123456"
  if not defined LEASE_REDIS_PORT set "LEASE_REDIS_PORT=6379"

  echo [1/6] Using local MySQL and Redis...
  call :ensure_local_mysql
  if errorlevel 1 exit /b 1

  echo [2/6] Waiting for local MySQL on 127.0.0.1:%LEASE_DB_PORT%...
  call :wait_for_port %LEASE_DB_PORT% 20 "MySQL"
  if errorlevel 1 exit /b 1

  echo [3/6] Initializing local schema if needed...
  call :init_local_mysql
  if errorlevel 1 exit /b 1

  echo [4/6] Waiting for local Redis on 127.0.0.1:%LEASE_REDIS_PORT%...
  call :wait_for_port %LEASE_REDIS_PORT% 10 "Redis"
  if errorlevel 1 (
    echo [ERROR] Redis is not listening on port %LEASE_REDIS_PORT%.
    echo         Start your local redis-server or rerun with --docker after freeing 6379.
    exit /b 1
  )
)

echo [5/6] Starting backend service...
start "Lease Backend" /D "%ROOT_DIR%\backend\lease-backend" cmd /k "set \"LEASE_DB_URL=jdbc:mysql://127.0.0.1:%LEASE_DB_PORT%/lease_db?useUnicode=true^&characterEncoding=utf-8^&serverTimezone=Asia/Shanghai^&useSSL=false^&allowPublicKeyRetrieval=true\" && set \"LEASE_DB_USERNAME=%LEASE_DB_USERNAME%\" && set \"LEASE_DB_PASSWORD=%LEASE_DB_PASSWORD%\" && set \"LEASE_REDIS_HOST=127.0.0.1\" && set \"LEASE_REDIS_PORT=%LEASE_REDIS_PORT%\" && mvn spring-boot:run"

echo [6/6] Starting admin console...
if not exist "%ROOT_DIR%\admin\lease-admin\node_modules" (
  echo [lease-admin] Installing npm dependencies...
  pushd "%ROOT_DIR%\admin\lease-admin"
  npm install
  if errorlevel 1 (
    popd
    echo [ERROR] npm install failed for admin console.
    exit /b 1
  )
  popd
)
start "Lease Admin" /D "%ROOT_DIR%\admin\lease-admin" cmd /k "npm run dev -- --host 127.0.0.1 --port 5173"

echo Opening mini program folder...
start "" explorer.exe "%ROOT_DIR%\miniprogram"

echo.
echo Services are being launched in separate windows:
echo   Backend: http://127.0.0.1:8081
echo   Admin:   http://127.0.0.1:5173
echo   MiniApp: open the ^"miniprogram^" folder in WeChat DevTools
echo.
if "%USE_DOCKER%"=="1" (
  echo If backend startup fails, check docker logs:
  echo   docker compose logs mysql
  echo   docker compose logs redis
) else (
  echo Local stack:
  echo   MySQL 127.0.0.1:%LEASE_DB_PORT%  user=%LEASE_DB_USERNAME%
  echo   Redis 127.0.0.1:%LEASE_REDIS_PORT%
)
echo.
exit /b 0

:ensure_port_free
set "CHECK_PORT=%~1"
set "CHECK_NAME=%~2"
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$conn = Get-NetTCPConnection -State Listen -LocalPort %CHECK_PORT% -ErrorAction SilentlyContinue | Select-Object -First 1; if ($conn) { exit 1 } else { exit 0 }" >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Port %CHECK_PORT% is already in use before Docker startup.
  echo         Docker %CHECK_NAME% needs this port on the host.
  echo         Stop the local process using %CHECK_PORT% and rerun, or use:
  echo           start-dev.bat --local
  exit /b 1
)
exit /b 0

:help
echo Usage: start-dev.bat
echo.
echo Default mode:
echo   Uses docker-compose MySQL/Redis.
echo.
echo Optional:
echo   start-dev.bat --local
echo     Uses local MySQL/Redis on this machine instead.
echo.
echo Starts:
echo   1. Infrastructure ^(docker by default, local with --local^)
echo   2. Spring Boot backend
echo   3. Vue admin dev server
echo   4. Opens the mini program folder
echo.
exit /b 0

:require_command
where %~1 >nul 2>&1
if errorlevel 1 (
  echo [ERROR] %~2 is not available in PATH.
  exit /b 1
)
exit /b 0

:resolve_docker_compose
docker compose version >nul 2>&1
if not errorlevel 1 (
  set "DOCKER_COMPOSE_CMD=docker compose"
  exit /b 0
)

where docker-compose >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Neither ^"docker compose^" nor ^"docker-compose^" is available.
  exit /b 1
)
set "DOCKER_COMPOSE_CMD=docker-compose"
exit /b 0

:ensure_local_mysql
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$svc = Get-Service -Name 'MySQL80' -ErrorAction SilentlyContinue; if ($svc -and $svc.Status -ne 'Running') { Start-Service -Name 'MySQL80' }; exit 0" >nul 2>&1
exit /b 0

:init_local_mysql
set "MYSQL_EXE=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
if not exist "%MYSQL_EXE%" (
  echo [ERROR] mysql.exe was not found at:
  echo         %MYSQL_EXE%
  exit /b 1
)

"%MYSQL_EXE%" -h 127.0.0.1 -P %LEASE_DB_PORT% -u %LEASE_DB_USERNAME% -p%LEASE_DB_PASSWORD% -e "CREATE DATABASE IF NOT EXISTS lease_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Failed to connect to local MySQL with current credentials.
  echo         Port: %LEASE_DB_PORT%, User: %LEASE_DB_USERNAME%
  echo         If needed, set LEASE_DB_USERNAME / LEASE_DB_PASSWORD before running.
  exit /b 1
)

type "%ROOT_DIR%\backend\lease-backend\src\main\resources\schema.sql" | "%MYSQL_EXE%" -h 127.0.0.1 -P %LEASE_DB_PORT% -u %LEASE_DB_USERNAME% -p%LEASE_DB_PASSWORD% lease_db >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Failed to initialize lease_db schema in local MySQL.
  exit /b 1
)
exit /b 0

:wait_for_port
set "TARGET_PORT=%~1"
set "WAIT_SECONDS=%~2"
set "SERVICE_NAME=%~3"
set "SERVICE_ID=%~4"
set /a ELAPSED=0

:wait_loop
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$client = $null; try { $client = New-Object Net.Sockets.TcpClient; $client.Connect('127.0.0.1', %TARGET_PORT%); exit 0 } catch { exit 1 } finally { if ($client) { $client.Close() } }" >nul 2>&1
if not errorlevel 1 (
  echo [OK] %SERVICE_NAME% is ready.
  exit /b 0
)

if %ELAPSED% GEQ %WAIT_SECONDS% (
  echo [ERROR] Timeout waiting for %SERVICE_NAME% on port %TARGET_PORT%.
  if defined SERVICE_ID (
    echo         You can inspect docker logs and retry:
    echo         %DOCKER_COMPOSE_CMD% logs %SERVICE_ID%
  )
  exit /b 1
)

timeout /t 2 /nobreak >nul
set /a ELAPSED+=2
goto :wait_loop
