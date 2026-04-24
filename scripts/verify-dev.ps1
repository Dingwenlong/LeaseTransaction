param(
  [switch]$SkipDockerBuild
)

$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

$Root = Resolve-Path (Join-Path $PSScriptRoot "..")

function Invoke-Step {
  param(
    [string]$Name,
    [scriptblock]$Script
  )

  Write-Host ""
  Write-Host "==> $Name" -ForegroundColor Cyan
  & $Script
}

function Require-Docker {
  docker info *> $null
  if ($LASTEXITCODE -ne 0) {
    throw "Docker Desktop is not running. Start Docker Desktop, then rerun scripts/verify-dev.ps1."
  }
}

Invoke-Step "Docker Compose config" {
  Push-Location $Root
  docker compose config --quiet
  Pop-Location
}

Invoke-Step "Backend tests on JDK 21 Docker image" {
  Require-Docker
  $backendPath = Join-Path $Root "backend"
  docker run --rm `
    -v "${backendPath}:/workspace" `
    -v "lease_maven_repo:/root/.m2" `
    -w /workspace `
    maven:3.9.9-eclipse-temurin-21 `
    mvn -B test
}

Invoke-Step "Admin build" {
  Push-Location (Join-Path $Root "admin")
  if (Test-Path "package-lock.json") {
    npm ci
  } else {
    npm install
  }
  npm run build
  Pop-Location
}

Invoke-Step "Regression marker scan" {
  Push-Location $Root
  rg -n "getCurrentUserIdOrDefault|demoMode|mock_pay_sign|backend/lease-backend|admin/lease-admin|root_password" -S . `
    --glob "!admin/node_modules/**" `
    --glob "!backend/target/**" `
    --glob "!scripts/verify-dev.ps1"
  if ($LASTEXITCODE -eq 0) {
    throw "Regression markers were found. Review the output above."
  }
  if ($LASTEXITCODE -gt 1) {
    throw "Marker scan failed."
  }
  Pop-Location
}

if (-not $SkipDockerBuild) {
  Invoke-Step "Docker Compose image build" {
    Push-Location $Root
    docker compose build
    Pop-Location
  }
}

Write-Host ""
Write-Host "Verification completed." -ForegroundColor Green
