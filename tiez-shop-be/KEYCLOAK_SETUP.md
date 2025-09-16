# Hướng dẫn cấu hình Keycloak cho Tiez Shop

## 1. Cài đặt và khởi động Keycloak

### Cách 1: Sử dụng Docker (Khuyến nghị)
```bash
# Tạo network cho Keycloak
docker network create keycloak-network

# Chạy Keycloak với PostgreSQL
docker run -d \
  --name keycloak \
  --network keycloak-network \
  -p 8180:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin123 \
  -e KC_DB=postgres \
  -e KC_DB_URL=jdbc:postgresql://postgres:5432/keycloak \
  -e KC_DB_USERNAME=keycloak \
  -e KC_DB_PASSWORD=keycloak123 \
  quay.io/keycloak/keycloak:latest \
  start-dev

# Chạy PostgreSQL cho Keycloak
docker run -d \
  --name postgres \
  --network keycloak-network \
  -p 5433:5432 \
  -e POSTGRES_DB=keycloak \
  -e POSTGRES_USER=keycloak \
  -e POSTGRES_PASSWORD=keycloak123 \
  postgres:13
```

### Cách 2: Download và chạy trực tiếp
1. Tải Keycloak từ: https://www.keycloak.org/downloads
2. Giải nén và chạy:
```bash
cd keycloak-21.0.0/bin
./kc.sh start-dev --http-port=8180
```

## 2. Cấu hình Realm và Client

### Bước 1: Truy cập Keycloak Admin Console
- URL: http://localhost:8180
- Username: `admin`
- Password: `admin123`

### Bước 2: Tạo Realm
1. Click vào dropdown "Master" ở góc trên bên trái
2. Click "Create Realm"
3. Tên realm: `tiez-shop`
4. Click "Create"

### Bước 3: Tạo Client
1. Trong realm `tiez-shop`, chọn "Clients" từ menu bên trái
2. Click "Create client"
3. Client ID: `tienngv`
4. Client protocol: `openid-connect`
5. Click "Next"
6. Client authentication: `ON` (Confidential)
7. Authorization: `OFF`
8. Authentication flow: `Standard flow`, `Direct access grants`
9. Click "Next"
10. **Valid redirect URIs**: 
    - `http://localhost:5173/callback`
    - `http://localhost:5173/*`
11. **Valid post logout redirect URIs**: `http://localhost:5173/logout`
12. **Web origins**: `http://localhost:5173`
13. Click "Save"

### Bước 4: Lấy Client Secret
1. Trong client `tienngv`, chọn tab "Credentials"
2. Copy "Client secret" (sẽ dùng trong application.yml)

## 3. Tạo User Demo

### Bước 1: Tạo User
1. Chọn "Users" từ menu bên trái
2. Click "Create new user"
3. Username: `demo@tiezshop.com`
4. Email: `demo@tiezshop.com`
5. First name: `Demo`
6. Last name: `User`
7. Email verified: `ON`
8. Enabled: `ON`
9. Click "Create"

### Bước 2: Đặt Password
1. Trong user vừa tạo, chọn tab "Credentials"
2. Click "Set password"
3. Password: `demo123`
4. Temporary: `OFF`
5. Click "Save"

## 4. Cấu hình Backend

### Kiểm tra application.yml
Đảm bảo cấu hình Keycloak trong `application.yml` đúng:

```yaml
keycloak:
  client-id: tienngv
  client-secret: YOUR_CLIENT_SECRET_HERE  # Lấy từ Keycloak Admin Console
  realm: tiez-shop
  base-url: http://localhost:8180
  registration-url: "${keycloak.base-url}/admin/realms/${keycloak.realm}/users"
  token-url: "${keycloak.base-url}/realms/${keycloak.realm}/protocol/openid-connect/token"
  assign-role-url: "${keycloak.base-url}/admin/realms/${keycloak.realm}/users/{userId}/role-mappings/realm"
  role-url: "${keycloak.base-url}/admin/realms/${keycloak.realm}/roles"
```

## 5. Test Authentication

### Test với curl
```bash
curl -X POST http://localhost:8180/realms/tiez-shop/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=tienngv" \
  -d "client_secret=YOUR_CLIENT_SECRET" \
  -d "username=demo@tiezshop.com" \
  -d "password=demo123"
```

### Test với Frontend
1. Khởi động backend: `mvn spring-boot:run`
2. Khởi động frontend: `npm run dev`
3. Truy cập http://localhost:5173/login
4. Click "Đăng nhập với Keycloak"
5. Đăng nhập trên Keycloak với:
   - Username: `demo@tiezshop.com`
   - Password: `demo123`
6. Sẽ redirect về frontend và đăng nhập thành công

## 6. Troubleshooting

### Lỗi thường gặp:

1. **Keycloak không khởi động được**
   - Kiểm tra port 8180 có bị chiếm không
   - Kiểm tra logs của Keycloak

2. **Lỗi 401 Unauthorized**
   - Kiểm tra client secret có đúng không
   - Kiểm tra realm name có đúng không
   - Kiểm tra user có tồn tại trong Keycloak không

3. **Lỗi 400 Bad Request**
   - Kiểm tra client ID có đúng không
   - Kiểm tra redirect URI có đúng không

4. **Lỗi kết nối database**
   - Kiểm tra PostgreSQL có chạy không
   - Kiểm tra connection string có đúng không

### Logs hữu ích:
- Backend logs: Kiểm tra console khi chạy `mvn spring-boot:run`
- Keycloak logs: Kiểm tra console của Keycloak
- Browser Network tab: Kiểm tra API calls

## 7. Cấu hình Production

### Security Considerations:
1. Thay đổi password admin mặc định
2. Sử dụng HTTPS trong production
3. Cấu hình proper CORS
4. Sử dụng strong client secret
5. Enable audit logging

### Performance:
1. Cấu hình connection pooling
2. Sử dụng Redis cho session storage
3. Enable clustering nếu cần scale
