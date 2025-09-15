# Hướng dẫn cấu hình Keycloak cho Tiez Shop

## Bước 1: Truy cập Keycloak Admin Console

1. Mở trình duyệt và truy cập: `http://localhost:8180`
2. Đăng nhập với admin account
3. Chọn realm `tiez-shop` (hoặc tạo mới nếu chưa có)

## Bước 2: Tạo/Cấu hình Client

### Tạo Client mới (nếu chưa có):

1. Vào **Clients** → **Create**
2. Điền thông tin:
   - **Client ID**: `tienngv`
   - **Client Protocol**: `openid-connect`
   - **Root URL**: `http://localhost:5173`

### Cấu hình Client (nếu đã có):

1. Vào **Clients** → chọn client `tienngv`
2. Vào tab **Settings** và cấu hình:

   **General Settings:**
   - **Client ID**: `tienngv`
   - **Name**: `Tiez Shop Frontend`
   - **Client Protocol**: `openid-connect`
   - **Access Type**: `public`
   - **Standard Flow Enabled**: `ON`
   - **Implicit Flow Enabled**: `OFF`
   - **Direct Access Grants Enabled**: `ON`
   - **Service Accounts Enabled**: `OFF`
   - **Authorization Enabled**: `OFF`

   **Login Settings:**
   - **Root URL**: `http://localhost:5173`
   - **Home URL**: `http://localhost:5173`
   - **Valid Redirect URIs**: 
     ```
     http://localhost:5173/*
     http://localhost:5173
     http://localhost:5173/silent-check-sso.html
     ```
   - **Valid Post Logout Redirect URIs**: 
     ```
     http://localhost:5173/*
     http://localhost:5173
     ```
   - **Web Origins**: 
     ```
     http://localhost:5173
     ```

## Bước 3: Tạo User (nếu cần)

1. Vào **Users** → **Add user**
2. Điền thông tin:
   - **Username**: `testuser`
   - **Email**: `test@example.com`
   - **First Name**: `Test`
   - **Last Name**: `User`
   - **Email Verified**: `ON`
3. Vào tab **Credentials** → **Set password**
4. Đặt password và tắt **Temporary**

## Bước 4: Kiểm tra cấu hình

### Chạy debug script:

1. Mở browser console (F12)
2. Chạy script debug:
   ```javascript
   // Copy và paste nội dung từ src/utils/keycloak-debug.js
   ```

### Kiểm tra thủ công:

1. Truy cập: `http://localhost:8180/realms/tiez-shop/.well-known/openid_configuration`
2. Kiểm tra response có chứa thông tin realm

## Bước 5: Test đăng nhập

1. Chạy ứng dụng: `npm run dev`
2. Truy cập: `http://localhost:5173`
3. Click "Đăng nhập với Keycloak"
4. Kiểm tra có redirect đến Keycloak login page không

## Troubleshooting

### Lỗi "Invalid parameter: redirect_uri"

**Nguyên nhân:** Client chưa được cấu hình đúng redirect URIs

**Giải pháp:**
1. Kiểm tra **Valid Redirect URIs** trong Keycloak client settings
2. Đảm bảo có các URI sau:
   - `http://localhost:5173/*`
   - `http://localhost:5173`
   - `http://localhost:5173/silent-check-sso.html`

### Lỗi "Invalid client"

**Nguyên nhân:** Client ID không tồn tại hoặc sai

**Giải pháp:**
1. Kiểm tra Client ID trong `src/services/keycloak.js`
2. Đảm bảo client tồn tại trong Keycloak realm

### Lỗi "Realm not found"

**Nguyên nhân:** Realm không tồn tại

**Giải pháp:**
1. Tạo realm `tiez-shop` trong Keycloak
2. Hoặc cập nhật realm name trong code

### Keycloak server không accessible

**Nguyên nhân:** Keycloak server chưa chạy hoặc sai port

**Giải pháp:**
1. Kiểm tra Keycloak server đang chạy trên port 8180
2. Kiểm tra URL trong `src/services/keycloak.js`

## Cấu hình Production

Khi deploy lên production, cần cập nhật:

1. **Keycloak URL**: Thay `http://localhost:8180` bằng production URL
2. **Redirect URIs**: Thay `http://localhost:5173` bằng production domain
3. **Web Origins**: Thay `http://localhost:5173` bằng production domain
4. **HTTPS**: Đảm bảo sử dụng HTTPS trong production
