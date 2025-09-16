# 🏪 Tiez Shop - E-commerce Backend System

## 📋 Tổng quan hệ thống

Tiez Shop là hệ thống backend cho shop thời trang chuyên bán quần áo, giày dép thể thao của các thương hiệu lớn như Adidas, Nike, Puma, v.v.

## 🏗️ Kiến trúc hệ thống

### Core Entities

#### 1. **Brand** - Thương hiệu
- Quản lý các thương hiệu thể thao (Adidas, Nike, Puma, etc.)
- Thông tin: tên, mô tả, logo, quốc gia
- Trạng thái active/inactive

#### 2. **Category** - Danh mục sản phẩm
- Hệ thống phân cấp danh mục (parent-child)
- Ví dụ: Thể thao > Giày dép > Giày chạy bộ
- Hỗ trợ subcategories

#### 3. **Product** - Sản phẩm
- Thông tin chi tiết sản phẩm
- Liên kết với Brand và Category
- Hỗ trợ giảm giá, đếm lượt xem, lượt bán
- Phân loại theo giới tính, màu sắc, kích thước

#### 4. **Inventory** - Kho hàng
- Quản lý tồn kho theo từng sản phẩm
- Theo dõi số lượng có sẵn và đã đặt trước
- Hỗ trợ nhiều màu sắc và kích thước

#### 5. **Order & OrderItem** - Đơn hàng
- Quản lý đơn hàng và chi tiết đơn hàng
- Theo dõi trạng thái đơn hàng và thanh toán
- Thông tin giao hàng chi tiết

#### 6. **ProductImage** - Hình ảnh sản phẩm
- Quản lý nhiều hình ảnh cho mỗi sản phẩm
- Hỗ trợ hình ảnh chính và sắp xếp thứ tự

#### 7. **Review** - Đánh giá sản phẩm
- Hệ thống đánh giá và bình luận
- Rating từ 1-5 sao
- Theo dõi lượt hữu ích

## 🚀 API Endpoints

### Brand Management
```
GET    /api/brands              - Lấy danh sách thương hiệu
POST   /api/brands              - Tạo thương hiệu mới
GET    /api/brands/{id}         - Lấy thông tin thương hiệu
PUT    /api/brands/{id}         - Cập nhật thương hiệu
DELETE /api/brands/{id}         - Xóa thương hiệu
GET    /api/brands/active       - Lấy danh sách thương hiệu đang hoạt động
GET    /api/brands/search       - Tìm kiếm thương hiệu
```

### Product Management
```
GET    /api/products            - Lấy danh sách sản phẩm
POST   /api/products            - Tạo sản phẩm mới
GET    /api/products/{id}        - Lấy thông tin sản phẩm
PUT    /api/products/{id}        - Cập nhật sản phẩm
DELETE /api/products/{id}       - Xóa sản phẩm
GET    /api/products/featured   - Sản phẩm nổi bật
GET    /api/products/search     - Tìm kiếm sản phẩm
GET    /api/products/filter     - Lọc sản phẩm
GET    /api/products/on-sale    - Sản phẩm đang giảm giá
GET    /api/products/top-viewed - Sản phẩm xem nhiều nhất
GET    /api/products/top-selling - Sản phẩm bán chạy nhất
```

## 🔍 Tính năng chính

### 1. **Quản lý sản phẩm**
- CRUD operations cho sản phẩm
- Tìm kiếm và lọc sản phẩm
- Quản lý hình ảnh sản phẩm
- Theo dõi lượt xem và lượt bán

### 2. **Quản lý kho hàng**
- Theo dõi tồn kho theo màu sắc và kích thước
- Quản lý số lượng đặt trước
- Kiểm tra tình trạng còn hàng

### 3. **Hệ thống đánh giá**
- Rating và comment sản phẩm
- Xác thực đánh giá từ khách hàng đã mua
- Theo dõi lượt hữu ích

### 4. **Quản lý đơn hàng**
- Tạo và theo dõi đơn hàng
- Quản lý trạng thái đơn hàng
- Thông tin giao hàng chi tiết

## 🛠️ Công nghệ sử dụng

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **PostgreSQL** (Database)
- **Maven** (Build tool)
- **Lombok** (Code generation)
- **MapStruct** (Object mapping)
- **Jakarta Validation** (Input validation)

## 📊 Database Schema

### Relationships
- Brand (1) → Product (N)
- Category (1) → Product (N)
- Product (1) → ProductImage (N)
- Product (1) → Inventory (N)
- Product (1) → Review (N)
- User (1) → Order (N)
- Order (1) → OrderItem (N)
- Product (1) → OrderItem (N)

## 🎯 Business Logic

### 1. **Product Management**
- Tự động tính phần trăm giảm giá
- Theo dõi lượt xem khi khách hàng xem chi tiết
- Cập nhật lượt bán khi đơn hàng hoàn thành

### 2. **Inventory Management**
- Kiểm tra tồn kho trước khi đặt hàng
- Tự động cập nhật số lượng đặt trước
- Cảnh báo khi hết hàng

### 3. **Order Processing**
- Tính toán tổng tiền bao gồm thuế và phí ship
- Quản lý trạng thái đơn hàng
- Hỗ trợ hủy đơn hàng trong trạng thái phù hợp

## 🔐 Security Features

- Input validation với Jakarta Validation
- Error handling tập trung
- Logging đầy đủ cho audit trail
- Transaction management an toàn

## 📈 Performance Optimizations

- Lazy loading cho relationships
- Pagination cho danh sách lớn
- Indexing trên các trường tìm kiếm
- Caching cho dữ liệu thường xuyên truy cập

## 🚀 Deployment

Hệ thống được thiết kế để deploy trên:
- **Docker containers**
- **Cloud platforms** (AWS, GCP, Azure)
- **Traditional servers**

## 📝 Next Steps

1. Implement Service implementations
2. Add comprehensive error handling
3. Implement caching layer
4. Add unit tests và integration tests
5. Setup CI/CD pipeline
6. Add monitoring và logging
7. Implement payment integration
8. Add admin dashboard APIs
