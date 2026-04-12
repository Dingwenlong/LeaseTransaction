$env:LEASE_DB_URL="jdbc:mysql://127.0.0.1:3306/lease_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
$env:LEASE_DB_USERNAME="root"
$env:LEASE_DB_PASSWORD="root_password"
$env:LEASE_REDIS_HOST="127.0.0.1"
$env:LEASE_REDIS_PORT="6379"
cd c:\Devs\LeaseTransaction\LeaseTransaction\backend\lease-backend
mvn spring-boot:run
