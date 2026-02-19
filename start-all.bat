@echo off
echo Starting Industrial Marketplace...
echo ================================
echo.

REM Get the current directory (where this batch file is located)
set PROJECT_ROOT=%~dp0
echo Project Root: %PROJECT_ROOT%

REM Remove trailing backslash
if "%PROJECT_ROOT:~-1%"=="\" set PROJECT_ROOT=%PROJECT_ROOT:~0,-1%

echo.
echo Starting Auth Service...
start "Auth Service" cmd /k "cd /d "%PROJECT_ROOT%\backend\auth-service" && .\mvnw.cmd spring-boot:run"
timeout /t 10

echo Starting Product Service...
start "Product Service" cmd /k "cd /d "%PROJECT_ROOT%\backend\product-service" && .\mvnw.cmd spring-boot:run"
timeout /t 5

echo Starting Vendor Service...
start "Vendor Service" cmd /k "cd /d "%PROJECT_ROOT%\backend\vendor-service" && .\mvnw.cmd spring-boot:run"
timeout /t 5

echo Starting Gateway...
start "Gateway" cmd /k "cd /d "%PROJECT_ROOT%\backend\gateway" && .\mvnw.cmd spring-boot:run"
timeout /t 10

echo Starting Frontend...
start "Frontend" cmd /k "cd /d "%PROJECT_ROOT%\frontend" && npm start"

echo.
echo All services started!
echo Frontend will open at http://localhost:3000
echo.
echo Close each window individually to stop services.
echo.
pause