# TiezShop Frontend

Frontend cho ứng dụng TiezShop được xây dựng bằng Vue 3 với tích hợp đầy đủ với backend Spring Boot.

## 🚀 Tính năng chính

### 🛍️ Quản lý sản phẩm
- **Trang chủ**: Hiển thị sản phẩm nổi bật từ API
- **Danh sách sản phẩm**: Tìm kiếm, lọc theo thương hiệu, danh mục, giá, giới tính
- **Chi tiết sản phẩm**: Thông tin đầy đủ, hình ảnh, thông số kỹ thuật
- **Phân trang**: Hỗ trợ phân trang với navigation

### 🏷️ Quản lý thương hiệu và danh mục
- **Dropdown thương hiệu**: Tự động load từ API
- **Bộ lọc động**: Categories và brands được load từ backend
- **Navigation thông minh**: URL parameters cho deep linking

### 🛒 Giỏ hàng và người dùng
- **Giỏ hàng**: Thêm/xóa sản phẩm, quản lý số lượng
- **Xác thực**: Tích hợp Keycloak cho đăng nhập/đăng ký
- **Profile**: Quản lý thông tin cá nhân

### 🎨 UI/UX
- **Responsive Design**: Tối ưu cho desktop, tablet, mobile
- **Loading States**: Spinner và error handling
- **Modern UI**: Thiết kế hiện đại với animations
- **Accessibility**: Hỗ trợ keyboard navigation

## 🛠️ Công nghệ sử dụng

- **Vue 3** - Framework chính
- **Vue Router 4** - Client-side routing
- **Pinia** - State management
- **Axios** - HTTP client
- **Keycloak JS** - Authentication
- **Vite** - Build tool

## 📁 Cấu trúc dự án

```
tiez-shop-fe/
├── src/
│   ├── components/          # Reusable components
│   │   ├── Layout.vue       # Main layout
│   │   ├── LoadingSpinner.vue
│   │   └── ErrorMessage.vue
│   ├── views/               # Page components
│   │   ├── Home.vue         # Trang chủ
│   │   ├── Products.vue     # Danh sách sản phẩm
│   │   ├── ProductDetail.vue # Chi tiết sản phẩm
│   │   ├── Cart.vue         # Giỏ hàng
│   │   ├── Profile.vue      # Profile
│   │   └── Orders.vue       # Đơn hàng
│   ├── services/            # API services
│   │   ├── api.js          # API client
│   │   └── keycloak.js     # Authentication
│   ├── stores/              # Pinia stores
│   │   ├── auth.js         # Authentication store
│   │   └── cart.js         # Cart store
│   ├── router/              # Vue Router
│   │   └── index.js        # Route configuration
│   └── utils/               # Utility functions
├── public/                  # Static assets
└── package.json            # Dependencies
```

## 🔧 Cài đặt và chạy

### Yêu cầu hệ thống
- Node.js 16+ 
- npm hoặc yarn
- Backend TiezShop đang chạy trên port 8080

### Cài đặt dependencies
```bash
cd tiez-shop-fe
npm install
```

### Chạy development server
```bash
npm run dev
```

Ứng dụng sẽ chạy trên `http://localhost:5173`

### Build cho production
```bash
npm run build
```

## 🔌 Tích hợp API

### Endpoints được sử dụng

#### Products API
- `GET /api/products` - Lấy danh sách sản phẩm
- `GET /api/products/{id}` - Chi tiết sản phẩm
- `GET /api/products/featured` - Sản phẩm nổi bật
- `GET /api/products/search` - Tìm kiếm sản phẩm
- `GET /api/products/filter` - Lọc sản phẩm

#### Brands API
- `GET /api/brands/active` - Thương hiệu đang hoạt động
- `GET /api/brands/{id}` - Chi tiết thương hiệu

#### Categories API
- `GET /api/categories/active` - Danh mục đang hoạt động
- `GET /api/categories/{id}` - Chi tiết danh mục

#### User API
- `POST /user/register` - Đăng ký
- `POST /user/login` - Đăng nhập
- `GET /user/{id}` - Thông tin user
- `PUT /user/{id}` - Cập nhật user

### Cấu hình API
API base URL được cấu hình trong `src/services/api.js`:
```javascript
const API_BASE_URL = 'http://localhost:8080'
```

## 🎯 Tính năng nổi bật

### 1. Tìm kiếm và lọc thông minh
- Tìm kiếm theo tên, mô tả, thương hiệu
- Lọc theo giá, thương hiệu, danh mục, giới tính
- URL parameters cho deep linking
- Real-time filtering

### 2. Responsive Design
- Mobile-first approach
- Breakpoints: 768px, 1920px, 2560px
- Flexible grid layouts
- Touch-friendly interactions

### 3. Error Handling
- Graceful error handling với retry functionality
- Loading states cho tất cả API calls
- Fallback data khi API không khả dụng
- User-friendly error messages

### 4. Performance Optimization
- Lazy loading cho images
- Pagination để giảm tải
- Efficient state management
- Minimal re-renders

## 🔐 Authentication

Tích hợp với Keycloak cho:
- Đăng nhập/đăng xuất
- Đăng ký tài khoản
- Token management
- Protected routes

## 🛒 Cart Management

- Thêm/xóa sản phẩm
- Quản lý số lượng
- Persistent storage
- Real-time updates

## 📱 Mobile Support

- Responsive design
- Touch gestures
- Mobile-optimized navigation
- Fast loading

## 🚀 Deployment

### Development
```bash
npm run dev
```

### Production Build
```bash
npm run build
```

### Preview Production Build
```bash
npm run preview
```

## 🤝 Contributing

1. Fork repository
2. Tạo feature branch
3. Commit changes
4. Push to branch
5. Tạo Pull Request

## 📄 License

MIT License - xem file LICENSE để biết thêm chi tiết.

## 🆘 Support

Nếu gặp vấn đề, vui lòng tạo issue trên GitHub repository.

---

**TiezShop Frontend** - Được xây dựng với ❤️ bằng Vue 3