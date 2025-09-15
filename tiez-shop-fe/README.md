# TiezShop Frontend

Dự án shop thời trang trực tuyến với Vue.js và tích hợp đăng nhập Keycloak.

## Tính năng

- 🛍️ **Shop bán hàng**: Trang chủ, danh sách sản phẩm, chi tiết sản phẩm
- 🛒 **Giỏ hàng**: Thêm/xóa sản phẩm, cập nhật số lượng
- 🔐 **Xác thực**: Đăng nhập với Keycloak và demo account
- 👤 **Quản lý người dùng**: Profile, đơn hàng, cài đặt
- 📱 **Responsive**: Giao diện thân thiện trên mọi thiết bị

## Công nghệ sử dụng

- **Vue 3** - Framework JavaScript
- **Vue Router** - Routing
- **Pinia** - State management
- **Keycloak JS** - Authentication
- **Axios** - HTTP client
- **Vite** - Build tool

## Cài đặt

### 1. Cài đặt dependencies

```bash
npm install
```

### 2. Cấu hình Keycloak

1. Cài đặt và chạy Keycloak server tại `http://localhost:8080`
2. Tạo realm tên `tiez-shop`
3. Tạo client tên `tiez-shop-frontend` với:
   - Client ID: `tiez-shop-frontend`
   - Root URL: `http://localhost:5173`
   - Valid Redirect URIs: `http://localhost:5173/*`
   - Web Origins: `http://localhost:5173`

### 3. Cập nhật cấu hình Keycloak

Chỉnh sửa file `src/services/keycloak.js`:

```javascript
const keycloakConfig = {
  url: 'http://localhost:8080', // URL Keycloak server
  realm: 'tiez-shop', // Tên realm
  clientId: 'tiez-shop-frontend' // Client ID
}
```

## Chạy dự án

### Development

```bash
npm run dev
```

Truy cập: `http://localhost:5173`

### Production

```bash
npm run build
npm run preview
```

## Cấu trúc dự án

```
src/
├── components/          # Components tái sử dụng
│   └── Layout.vue      # Layout chính
├── services/           # Services
│   └── keycloak.js     # Keycloak configuration
├── stores/            # Pinia stores
│   ├── auth.js        # Authentication store
│   └── cart.js        # Shopping cart store
├── views/             # Pages
│   ├── Home.vue       # Trang chủ
│   ├── Login.vue      # Đăng nhập
│   ├── Products.vue   # Danh sách sản phẩm
│   ├── ProductDetail.vue # Chi tiết sản phẩm
│   ├── Cart.vue       # Giỏ hàng
│   ├── Profile.vue    # Thông tin cá nhân
│   └── Orders.vue     # Đơn hàng
├── router/            # Vue Router
│   └── index.js       # Router configuration
├── App.vue            # Root component
└── main.js            # Entry point
```

## Tính năng đăng nhập

### Keycloak Authentication

- Đăng nhập với Keycloak server
- Tự động refresh token
- Role-based access control

### Demo Account

Để test nhanh, có thể sử dụng demo account:
- **Username**: demo@tiezshop.com
- **Password**: demo123
- **Role**: Customer

## API Integration

Dự án được thiết kế để tích hợp với backend API. Các endpoint cần thiết:

- `GET /api/products` - Lấy danh sách sản phẩm
- `GET /api/products/:id` - Lấy chi tiết sản phẩm
- `POST /api/orders` - Tạo đơn hàng
- `GET /api/orders` - Lấy danh sách đơn hàng
- `PUT /api/profile` - Cập nhật thông tin cá nhân

## Deployment

### Vercel/Netlify

1. Build project: `npm run build`
2. Deploy thư mục `dist/`
3. Cập nhật Keycloak configuration với production URL

### Docker

```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build
EXPOSE 3000
CMD ["npm", "run", "preview"]
```

## Troubleshooting

### Keycloak không kết nối được

1. Kiểm tra Keycloak server đang chạy
2. Kiểm tra realm và client configuration
3. Kiểm tra CORS settings trong Keycloak

### Lỗi routing

1. Đảm bảo Vue Router được cấu hình đúng
2. Kiểm tra base URL trong production

## Contributing

1. Fork repository
2. Tạo feature branch
3. Commit changes
4. Push và tạo Pull Request

## License

MIT License