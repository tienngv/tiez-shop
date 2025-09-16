@echo off
REM Script khởi động Keycloak với Docker cho Tiez Shop (Windows)

echo 🚀 Khởi động Keycloak cho Tiez Shop...

REM Tạo network nếu chưa có
docker network create keycloak-network 2>nul || echo Network đã tồn tại

REM Dừng và xóa containers cũ nếu có
docker stop keycloak postgres 2>nul
docker rm keycloak postgres 2>nul

echo 📦 Khởi động PostgreSQL...
docker run -d ^
  --name postgres ^
  --network keycloak-network ^
  -p 5433:5432 ^
  -e POSTGRES_DB=keycloak ^
  -e POSTGRES_USER=keycloak ^
  -e POSTGRES_PASSWORD=keycloak123 ^
  postgres:13

echo ⏳ Chờ PostgreSQL khởi động...
timeout /t 10 /nobreak >nul

echo 🔐 Khởi động Keycloak...
docker run -d ^
  --name keycloak ^
  --network keycloak-network ^
  -p 8180:8080 ^
  -e KEYCLOAK_ADMIN=admin ^
  -e KEYCLOAK_ADMIN_PASSWORD=admin123 ^
  -e KC_DB=postgres ^
  -e KC_DB_URL=jdbc:postgresql://postgres:5432/keycloak ^
  -e KC_DB_USERNAME=keycloak ^
  -e KC_DB_PASSWORD=keycloak123 ^
  quay.io/keycloak/keycloak:latest ^
  start-dev

echo ⏳ Chờ Keycloak khởi động...
timeout /t 30 /nobreak >nul

echo ✅ Keycloak đã khởi động!
echo.
echo 🌐 Truy cập Keycloak Admin Console:
echo    URL: http://localhost:8180
echo    Username: admin
echo    Password: admin123
echo.
echo 📋 Các bước tiếp theo:
echo    1. Tạo realm 'tiez-shop'
echo    2. Tạo client 'tienngv'
echo    3. Tạo user demo: demo@tiezshop.com / demo123
echo    4. Cập nhật client secret trong application.yml
echo.
echo 📖 Xem hướng dẫn chi tiết: KEYCLOAK_SETUP.md

pause
