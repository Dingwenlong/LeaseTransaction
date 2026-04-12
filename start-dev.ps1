$ErrorActionPreference = "Continue"
Write-Host ""
Write-Host "=========================================="
Write-Host "  Campus Lease Transaction Dev Launcher"
Write-Host "=========================================="
Write-Host ""

$ROOT_DIR = Split-Path -Parent $MyInvocation.MyCommand.Path
$BACKEND_PORT = 8081

Write-Host "[INFO] Checking Docker services..."
$mysqlRunning = docker ps --filter "name=campus-errand-mysql" --format "{{.Names}}" 2>$null | Select-String "campus-errand-mysql"
$leaseMysqlRunning = docker ps --filter "name=lease-mysql" --format "{{.Names}}" 2>$null | Select-String "lease-mysql"

if ($mysqlRunning -or $leaseMysqlRunning) {
    Write-Host "[OK] Docker MySQL is already running."
} else {
    Write-Host "[ERROR] No Docker MySQL found. Please start campus-errand-mysql container first."
    Write-Host "Press Enter to exit..."
    Read-Host
    exit 1
}

Write-Host "[2/5] Waiting for MySQL on 127.0.0.1:3306..."
$startTime = Get-Date
while ($true) {
    $client = New-Object Net.Sockets.TcpClient
    try {
        $client.Connect('127.0.0.1', 3306)
        $client.Close()
        Write-Host "[OK] MySQL is ready."
        break
    } catch {
        if ((Get-Date).Subtract($startTime).TotalSeconds -gt 60) {
            Write-Host "[ERROR] Timeout waiting for MySQL on port 3306."
            Write-Host "Press Enter to exit..."
            Read-Host
            exit 1
        }
        Start-Sleep -Seconds 2
    }
}

Write-Host "[3/5] Waiting for Redis on 127.0.0.1:6379..."
$startTime = Get-Date
while ($true) {
    $client = New-Object Net.Sockets.TcpClient
    try {
        $client.Connect('127.0.0.1', 6379)
        $client.Close()
        Write-Host "[OK] Redis is ready."
        break
    } catch {
        if ((Get-Date).Subtract($startTime).TotalSeconds -gt 60) {
            Write-Host "[ERROR] Timeout waiting for Redis on port 6379."
            Write-Host "Press Enter to exit..."
            Read-Host
            exit 1
        }
        Start-Sleep -Seconds 2
    }
}

Write-Host "[4/5] Starting backend service..."
Start-Process powershell -ArgumentList "-NoProfile","-ExecutionPolicy","Bypass","-File","$ROOT_DIR\run-backend.ps1" -WorkingDirectory "$ROOT_DIR\backend\lease-backend" -WindowStyle Normal

Write-Host "[5/5] Starting admin console..."
if (-not (Test-Path "$ROOT_DIR\admin\lease-admin\node_modules")) {
    Write-Host "[lease-admin] Installing npm dependencies..."
    Push-Location "$ROOT_DIR\admin\lease-admin"
    npm install
    Pop-Location
}
Start-Process cmd -ArgumentList "/c","npm run dev" -WorkingDirectory "$ROOT_DIR\admin\lease-admin" -WindowStyle Normal

Write-Host "Opening miniprogram folder..."
Start-Process explorer -ArgumentList "$ROOT_DIR\miniprogram"

Write-Host ""
Write-Host "=========================================="
Write-Host "  Services are being launched:"
Write-Host "  Backend: http://127.0.0.1:$BACKEND_PORT"
Write-Host "  Admin:   http://127.0.0.1:5173"
Write-Host "  MiniApp: Open miniprogram in WeChat DevTools"
Write-Host "=========================================="
Write-Host ""
Write-Host "Press Enter to exit..."
Read-Host
