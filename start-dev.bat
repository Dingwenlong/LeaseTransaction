@echo off
setlocal EnableExtensions EnableDelayedExpansion

set "ROOT_DIR=%~dp0"
if "%ROOT_DIR:~-1%"=="\" set "ROOT_DIR=%ROOT_DIR:~0,-1%"

set "RUN_MODE=auto"
if /I "%~1"=="--docker" set "RUN_MODE=docker"
if /I "%~1"=="--local" set "RUN_MODE=local"

if /I "%~1"=="-h" goto :help
if /I "%~1"=="--help" goto :help

set "LOCAL_DB_PORT=3307"
if defined LEASE_DB_PORT set "LOCAL_DB_PORT=%LEASE_DB_PORT%"
set "LOCAL_REDIS_PORT=6379"
if defined LEASE_REDIS_PORT set "LOCAL_REDIS_PORT=%LEASE_REDIS_PORT%"
set "ADMIN_PORT=5173"

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

echo [1/6] Selecting runtime mode...
call :choose_mode
if errorlevel 1 exit /b 1

if /I "%SELECTED_MODE%"=="docker" (
  echo [INFO] Mode: Docker
) else (
  if /I "%MODE_REASON%"=="fallback_local" (
    echo [WARN] Docker host ports are occupied. Falling back to local MySQL/Redis.
  )
  echo [INFO] Mode: Local
)
echo.

echo [2/6] Preparing infrastructure...
if /I "%SELECTED_MODE%"=="docker" (
  call :start_docker_stack
) else (
  call :start_local_stack
)
if errorlevel 1 exit /b 1

echo [3/6] Choosing admin port...
call :choose_admin_port 5173
if errorlevel 1 exit /b 1
echo [OK] Admin will use http://127.0.0.1:%ADMIN_PORT%

echo [4/6] Starting backend service...
call :start_backend
if errorlevel 1 exit /b 1

echo [5/6] Starting admin console...
call :start_admin
if errorlevel 1 exit /b 1

echo [6/6] Opening mini program folder...
start "" explorer.exe "%ROOT_DIR%\miniprogram"

echo.
echo Services are being launched in separate windows:
echo   Backend: http://127.0.0.1:8081
echo   Admin:   http://127.0.0.1:%ADMIN_PORT%
echo   MiniApp: open the ^"miniprogram^" folder in WeChat DevTools
echo.
if /I "%SELECTED_MODE%"=="docker" (
  echo Docker stack:
  echo   MySQL 127.0.0.1:3306  user=root
  echo   Redis 127.0.0.1:6379
  echo.
  echo If backend startup fails, inspect:
  echo   docker compose logs mysql
  echo   docker compose logs redis
) else (
  echo Local stack:
  echo   MySQL 127.0.0.1:%LEASE_DB_PORT%  user=%LEASE_DB_USERNAME%
  echo   Redis 127.0.0.1:%LEASE_REDIS_PORT%
)
echo.
exit /b 0

:choose_mode
set "SELECTED_MODE="
set "MODE_REASON="

if /I "%RUN_MODE%"=="docker" (
  set "SELECTED_MODE=docker"
  set "MODE_REASON=forced_docker"
  exit /b 0
)

if /I "%RUN_MODE%"=="local" (
  set "SELECTED_MODE=local"
  set "MODE_REASON=forced_local"
  exit /b 0
)

call :is_port_free 3306
set "DOCKER_MYSQL_FREE=%ERRORLEVEL%"
call :is_port_free 6379
set "DOCKER_REDIS_FREE=%ERRORLEVEL%"

if "%DOCKER_MYSQL_FREE%"=="0" if "%DOCKER_REDIS_FREE%"=="0" (
  set "SELECTED_MODE=docker"
  set "MODE_REASON=auto_docker"
  exit /b 0
)

call :can_connect 127.0.0.1 %LOCAL_DB_PORT%
set "LOCAL_MYSQL_READY=%ERRORLEVEL%"
call :can_connect 127.0.0.1 %LOCAL_REDIS_PORT%
set "LOCAL_REDIS_READY=%ERRORLEVEL%"

if "%LOCAL_MYSQL_READY%"=="0" if "%LOCAL_REDIS_READY%"=="0" (
  set "SELECTED_MODE=local"
  set "MODE_REASON=fallback_local"
  exit /b 0
)

echo [ERROR] Cannot select a runnable mode.
if not "%DOCKER_MYSQL_FREE%"=="0" call :describe_port_usage 3306 "MySQL"
if not "%DOCKER_REDIS_FREE%"=="0" call :describe_port_usage 6379 "Redis"
echo.
echo Neither Docker nor the local dependency stack is fully available.
echo   Docker requires host ports 3306 and 6379 to be free.
echo   Local mode requires MySQL on %LOCAL_DB_PORT% and Redis on %LOCAL_REDIS_PORT%.
echo.
echo Try one of these:
echo   1. Free ports 3306 and 6379, then rerun
echo   2. Start local MySQL and Redis, then rerun
echo   3. Force a mode explicitly:
echo      start-dev.bat --docker
echo      start-dev.bat --local
echo.
exit /b 1

:start_docker_stack
call :resolve_docker_compose
if errorlevel 1 exit /b 1

call :assert_port_free 3306 "MySQL"
if errorlevel 1 exit /b 1
call :assert_port_free 6379 "Redis"
if errorlevel 1 exit /b 1

set "LEASE_DB_PORT=3306"
if not defined LEASE_DB_USERNAME set "LEASE_DB_USERNAME=root"
if not defined LEASE_DB_PASSWORD set "LEASE_DB_PASSWORD=root"
set "LEASE_REDIS_PORT=6379"

pushd "%ROOT_DIR%"
%DOCKER_COMPOSE_CMD% up -d
if errorlevel 1 (
  popd
  echo [ERROR] Failed to start docker services.
  exit /b 1
)
popd

echo [INFO] Waiting for MySQL on 127.0.0.1:3306...
call :wait_for_port 3306 60 "MySQL" "mysql"
if errorlevel 1 exit /b 1

echo [INFO] Waiting for Redis on 127.0.0.1:6379...
call :wait_for_port 6379 60 "Redis" "redis"
if errorlevel 1 exit /b 1

exit /b 0

:start_local_stack
set "LEASE_DB_PORT=%LOCAL_DB_PORT%"
if not defined LEASE_DB_USERNAME set "LEASE_DB_USERNAME=root"
if not defined LEASE_DB_PASSWORD set "LEASE_DB_PASSWORD=123456"
set "LEASE_REDIS_PORT=%LOCAL_REDIS_PORT%"

call :ensure_local_mysql
if errorlevel 1 exit /b 1

echo [INFO] Waiting for local MySQL on 127.0.0.1:%LEASE_DB_PORT%...
call :wait_for_port %LEASE_DB_PORT% 20 "MySQL"
if errorlevel 1 exit /b 1

echo [INFO] Initializing local schema if needed...
call :init_local_mysql
if errorlevel 1 exit /b 1

echo [INFO] Waiting for local Redis on 127.0.0.1:%LEASE_REDIS_PORT%...
call :wait_for_port %LEASE_REDIS_PORT% 10 "Redis"
if errorlevel 1 (
  echo [ERROR] Redis is not listening on port %LEASE_REDIS_PORT%.
  echo         Start a local redis-server or rerun with --docker after freeing 6379.
  exit /b 1
)

exit /b 0

:start_backend
call :is_port_free 8081
if errorlevel 1 (
  echo [INFO] Backend is already listening on http://127.0.0.1:8081
  exit /b 0
)

set "BACKEND_BOOTSTRAP=%TEMP%\lease-start-backend.bat"
(
  echo @echo off
  echo set LEASE_DB_URL=jdbc:mysql://127.0.0.1:%LEASE_DB_PORT%/lease_db?useUnicode=true^&characterEncoding=utf-8^&serverTimezone=Asia/Shanghai^&useSSL=false^&allowPublicKeyRetrieval=true
  echo set "LEASE_DB_USERNAME=%LEASE_DB_USERNAME%"
  echo set "LEASE_DB_PASSWORD=%LEASE_DB_PASSWORD%"
  echo set "LEASE_REDIS_HOST=127.0.0.1"
  echo set "LEASE_REDIS_PORT=%LEASE_REDIS_PORT%"
  echo mvn spring-boot:run
) > "%BACKEND_BOOTSTRAP%"

start "Lease Backend" /D "%ROOT_DIR%\backend" cmd /k call "%BACKEND_BOOTSTRAP%"
exit /b 0

:start_admin
if not exist "%ROOT_DIR%\admin\node_modules" (
  echo [lease-admin] Installing npm dependencies...
  pushd "%ROOT_DIR%\admin"
  npm install
  if errorlevel 1 (
    popd
    echo [ERROR] npm install failed for admin console.
    exit /b 1
  )
  popd
)

start "Lease Admin" /D "%ROOT_DIR%\admin" cmd /k "npm run dev -- --host 127.0.0.1 --port %ADMIN_PORT%"
exit /b 0

:choose_admin_port
set "ADMIN_PORT=%~1"
:choose_admin_port_loop
call :is_port_free %ADMIN_PORT%
if not errorlevel 1 exit /b 0
set /a ADMIN_PORT+=1
if %ADMIN_PORT% GTR 5200 (
  echo [ERROR] Could not find a free admin port in the 5173-5200 range.
  exit /b 1
)
goto :choose_admin_port_loop

:is_port_free
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$conn = Get-NetTCPConnection -State Listen -LocalPort %~1 -ErrorAction SilentlyContinue | Select-Object -First 1; if ($conn) { exit 1 } else { exit 0 }" >nul 2>&1
exit /b %ERRORLEVEL%

:can_connect
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$client = $null; try { $client = New-Object Net.Sockets.TcpClient; $client.Connect('%~1', %~2); exit 0 } catch { exit 1 } finally { if ($client) { $client.Close() } }" >nul 2>&1
exit /b %ERRORLEVEL%

:assert_port_free
call :is_port_free %~1
if not errorlevel 1 exit /b 0
echo [ERROR] Port %~1 is already in use.
call :describe_port_usage %~1 "%~2"
echo         Free the port and rerun, or use:
echo           start-dev.bat --local
exit /b 1

:describe_port_usage
for /f "usebackq delims=" %%I in (`powershell -NoProfile -ExecutionPolicy Bypass -Command "$conn = Get-NetTCPConnection -State Listen -LocalPort %~1 -ErrorAction SilentlyContinue | Select-Object -First 1; if ($conn) { $proc = Get-Process -Id $conn.OwningProcess -ErrorAction SilentlyContinue; if ($proc) { Write-Output ('        - Port %~1 (%~2): ' + $proc.ProcessName + ' (PID ' + $conn.OwningProcess + ')') } else { Write-Output ('        - Port %~1 (%~2): PID ' + $conn.OwningProcess) } }"` ) do echo %%I
exit /b 0

:help
echo Usage: start-dev.bat [--docker ^| --local]
echo.
echo Default behavior:
echo   Prefer Docker.
echo   If Docker host ports are occupied and local MySQL/Redis are available,
echo   the script falls back to local mode automatically.
echo.
echo Options:
echo   --docker    Force Docker mode only
echo   --local     Force local mode only
echo   -h, --help  Show this help message
echo.
echo Environment variables:
echo   LEASE_DB_PORT       Local MySQL port override ^(default: 3307 in local mode^)
echo   LEASE_DB_USERNAME   MySQL username ^(default: root^)
echo   LEASE_DB_PASSWORD   MySQL password ^(default: root for Docker, 123456 for local^)
echo   LEASE_REDIS_PORT    Local Redis port override ^(default: 6379^)
echo.
echo Starts:
echo   1. MySQL + Redis ^(Docker preferred, local fallback when available^)
echo   2. Spring Boot backend      http://127.0.0.1:8081
echo   3. Vue admin dev server     http://127.0.0.1:5173 ^(or next free port^)
echo   4. Opens the mini program folder in explorer
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
set "MYSQL_EXE="
for /f "usebackq delims=" %%I in (`where mysql 2^>nul`) do (
  set "MYSQL_EXE=%%I"
  goto :mysql_cli_found
)
if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" (
  set "MYSQL_EXE=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
  goto :mysql_cli_found
)
echo [ERROR] mysql.exe was not found in PATH or the default install path.
exit /b 1

:mysql_cli_found
"%MYSQL_EXE%" -h 127.0.0.1 -P %LEASE_DB_PORT% -u %LEASE_DB_USERNAME% -p%LEASE_DB_PASSWORD% -e "CREATE DATABASE IF NOT EXISTS lease_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Failed to connect to local MySQL with current credentials.
  echo         Port: %LEASE_DB_PORT%, User: %LEASE_DB_USERNAME%
  echo         If needed, set LEASE_DB_USERNAME / LEASE_DB_PASSWORD before running.
  exit /b 1
)

type "%ROOT_DIR%\backend\src\main\resources\schema.sql" | "%MYSQL_EXE%" -h 127.0.0.1 -P %LEASE_DB_PORT% -u %LEASE_DB_USERNAME% -p%LEASE_DB_PASSWORD% lease_db >nul 2>&1
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

:wait_for_port_loop
call :can_connect 127.0.0.1 %TARGET_PORT%
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
goto :wait_for_port_loop
