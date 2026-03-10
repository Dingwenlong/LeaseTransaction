@echo off
chcp 936 >nul
echo ========================================
echo   Campus Lease Trading System - Startup
echo ========================================
echo.

REM Get script directory
set "SCRIPT_DIR=%~dp0"
set "PROJECT_DIR=%SCRIPT_DIR%.."
set "BACKEND_DIR=%PROJECT_DIR%\backend\lease-backend"
set "ADMIN_DIR=%PROJECT_DIR%\admin\lease-admin"

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not running. Please start Docker Desktop first.
    pause
    exit /b 1
)

echo [1/4] Starting Docker services (MySQL + Redis)...
cd /d "%PROJECT_DIR%"
docker-compose up -d

if errorlevel 1 (
    echo [ERROR] Failed to start Docker services.
    pause
    exit /b 1
)

echo [2/4] Waiting for services to be ready...
echo - Waiting for MySQL...
:wait_mysql
docker exec lease-mysql mysqladmin ping -h localhost -u root -proot >nul 2>&1
if errorlevel 1 (
    timeout /t 2 /nobreak >nul
    goto wait_mysql
)
echo - MySQL is ready!

echo - Waiting for Redis...
:wait_redis
docker exec lease-redis redis-cli ping >nul 2>&1
if errorlevel 1 (
    timeout /t 2 /nobreak >nul
    goto wait_redis
)
echo - Redis is ready!

timeout /t 3 /nobreak >nul

echo [3/4] Starting Backend Service...
start "Backend Service" cmd /k "cd /d "%BACKEND_DIR%" && mvn clean spring-boot:run"

timeout /t 5 /nobreak >nul

echo [4/4] Starting Admin Frontend...
cd /d "%ADMIN_DIR%"

REM Check if node_modules exists, if not install dependencies
if not exist "node_modules" (
    echo Installing dependencies...
    call npm install
)

start "Admin Frontend" cmd /k "npm run dev"

echo.
echo ========================================
echo   All services are starting...
echo ========================================
echo.
echo Docker Services:
echo - MySQL:     localhost:3306 (root/root)
echo - Redis:     localhost:6379
echo.
echo Application Services:
echo - Backend:   http://localhost:8081
echo - Frontend:  http://localhost:3000
echo.
echo Commands:
echo - View logs:  docker-compose logs -f
echo - Stop all:   docker-compose down
echo.
pause
