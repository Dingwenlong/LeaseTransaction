$ErrorActionPreference = "Stop"

$RootDir = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host ""
Write-Host "=========================================="
Write-Host "  Campus Lease Transaction Dev Launcher"
Write-Host "=========================================="
Write-Host ""

if (-not (Test-Path (Join-Path $RootDir ".env"))) {
    Copy-Item (Join-Path $RootDir ".env.example") (Join-Path $RootDir ".env")
    Write-Host "[INFO] Created .env from .env.example. Review secrets before production use."
}

Write-Host "[1/4] Starting MySQL and Redis..."
docker compose up -d mysql redis

function Wait-Port($Port, $Name) {
    $startTime = Get-Date
    while ($true) {
        $client = New-Object Net.Sockets.TcpClient
        try {
            $client.Connect("127.0.0.1", $Port)
            $client.Close()
            Write-Host "[OK] $Name is ready."
            return
        } catch {
            if ((Get-Date).Subtract($startTime).TotalSeconds -gt 90) {
                throw "Timeout waiting for $Name on port $Port"
            }
            Start-Sleep -Seconds 2
        }
    }
}

Write-Host "[2/4] Waiting for dependencies..."
Wait-Port 3306 "MySQL"
Wait-Port 6379 "Redis"

Write-Host "[3/4] Starting backend..."
Start-Process powershell -ArgumentList "-NoProfile","-ExecutionPolicy","Bypass","-File",(Join-Path $RootDir "run-backend.ps1") -WorkingDirectory $RootDir -WindowStyle Normal

Write-Host "[4/4] Starting admin console..."
if (-not (Test-Path (Join-Path $RootDir "admin\node_modules"))) {
    Push-Location (Join-Path $RootDir "admin")
    npm install
    Pop-Location
}
Start-Process cmd -ArgumentList "/c","npm run dev" -WorkingDirectory (Join-Path $RootDir "admin") -WindowStyle Normal

Write-Host ""
Write-Host "Backend: http://127.0.0.1:8081"
Write-Host "Admin:   http://127.0.0.1:5173"
Write-Host "MiniApp: open miniprogram in WeChat DevTools"
