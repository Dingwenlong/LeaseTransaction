$ErrorActionPreference = "Stop"

$RootDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$BackendDir = Join-Path $RootDir "backend"

if (Test-Path (Join-Path $RootDir ".env")) {
    Get-Content (Join-Path $RootDir ".env") | ForEach-Object {
        if ($_ -match "^\s*#" -or $_ -notmatch "=") { return }
        $parts = $_ -split "=", 2
        [Environment]::SetEnvironmentVariable($parts[0].Trim(), $parts[1].Trim(), "Process")
    }
}

function Set-DefaultEnv($Name, $Value) {
    if ([string]::IsNullOrWhiteSpace([Environment]::GetEnvironmentVariable($Name, "Process"))) {
        [Environment]::SetEnvironmentVariable($Name, $Value, "Process")
    }
}

Set-DefaultEnv "SERVER_PORT" "8081"
Set-DefaultEnv "LEASE_DB_NAME" "lease_db"
Set-DefaultEnv "LEASE_DB_PORT" "3307"
Set-DefaultEnv "LEASE_DB_USERNAME" "root"
Set-DefaultEnv "LEASE_DB_PASSWORD" "root"
Set-DefaultEnv "LEASE_DB_URL" "jdbc:mysql://127.0.0.1:$($env:LEASE_DB_PORT)/$($env:LEASE_DB_NAME)?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
Set-DefaultEnv "LEASE_REDIS_HOST" "127.0.0.1"
Set-DefaultEnv "LEASE_REDIS_PORT" "6379"
Set-DefaultEnv "SPRING_PROFILES_ACTIVE" "local"

Set-Location $BackendDir
mvn spring-boot:run
