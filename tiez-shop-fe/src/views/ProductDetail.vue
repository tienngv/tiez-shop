<template>
  <div class="product-detail">
    <div v-if="loading" class="loading">
      <p>Đang tải...</p>
    </div>
    
    <div v-else-if="product" class="product-container">
      <div class="product-images">
        <img :src="product.image" :alt="product.name" class="main-image">
        <div class="thumbnail-images">
          <img 
            v-for="(img, index) in productImages" 
            :key="index"
            :src="img" 
            :alt="product.name"
            class="thumbnail"
            @click="setMainImage(img)"
          >
        </div>
      </div>
      
      <div class="product-info">
        <div class="product-brand">{{ product.brand }}</div>
        <h1 class="product-title">{{ product.name }}</h1>
        <div class="product-rating">
          <span class="stars">★★★★☆</span>
          <span class="rating-text">({{ product.rating }}/5 - {{ product.reviews }} đánh giá)</span>
        </div>
        
        <div class="product-price">
          <span class="current-price">{{ formatPrice(product.price) }}</span>
          <span v-if="product.originalPrice" class="original-price">
            {{ formatPrice(product.originalPrice) }}
          </span>
          <span v-if="product.discount" class="discount">
            -{{ product.discount }}%
          </span>
        </div>
        
        <div class="product-description">
          <h3>Mô tả sản phẩm</h3>
          <p>{{ product.description }}</p>
        </div>
        
        <div class="product-specs">
          <h3>Thông số kỹ thuật</h3>
          <ul>
            <li v-for="spec in product.specifications" :key="spec.name">
              <strong>{{ spec.name }}:</strong> {{ spec.value }}
            </li>
          </ul>
        </div>
        
        <div class="product-actions">
          <div class="quantity-selector">
            <label>Số lượng:</label>
            <div class="quantity-controls">
              <button @click="decreaseQuantity" class="quantity-btn">-</button>
              <input v-model.number="quantity" type="number" min="1" class="quantity-input">
              <button @click="increaseQuantity" class="quantity-btn">+</button>
            </div>
          </div>
          
          <div class="action-buttons">
            <button @click="addToCart" class="add-to-cart-btn">
              Thêm vào giỏ hàng
            </button>
            <button @click="buyNow" class="buy-now-btn">
              Mua ngay
            </button>
          </div>
        </div>
        
        <div class="product-features">
          <div class="feature">
            <i class="feature-icon">🚚</i>
            <span>Miễn phí vận chuyển</span>
          </div>
          <div class="feature">
            <i class="feature-icon">🔄</i>
            <span>Đổi trả trong 30 ngày</span>
          </div>
          <div class="feature">
            <i class="feature-icon">🛡️</i>
            <span>Bảo hành chính hãng</span>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else class="not-found">
      <h2>Sản phẩm không tồn tại</h2>
      <router-link to="/products" class="back-to-products">
        Quay lại danh sách sản phẩm
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart.js'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const loading = ref(true)
const quantity = ref(1)
const productImages = ref([])

// Mock data - trong thực tế sẽ lấy từ API
const products = ref([
  {
    id: 1,
    name: 'Nike Air Force 1',
    price: 2890000,
    originalPrice: 3290000,
    discount: 12,
    image: 'https://via.placeholder.com/500x400?text=Nike+Air+Force+1',
    description: 'Nike Air Force 1 là một trong những mẫu giày thể thao cổ điển nhất của Nike. Với thiết kế đơn giản nhưng đầy phong cách, đây là lựa chọn hoàn hảo cho mọi phong cách thời trang.',
    category: 'shoes',
    brand: 'Nike',
    rating: 4.8,
    reviews: 1250,
    specifications: [
      { name: 'Thương hiệu', value: 'Nike' },
      { name: 'Dòng sản phẩm', value: 'Air Force 1' },
      { name: 'Chất liệu', value: 'Da tổng hợp cao cấp' },
      { name: 'Đế giày', value: 'Air-Sole unit' },
      { name: 'Màu sắc', value: 'Trắng cổ điển' },
      { name: 'Kích thước', value: '36-45' }
    ]
  },
  {
    id: 2,
    name: 'Adidas Ultraboost 22',
    price: 4590000,
    originalPrice: 4990000,
    discount: 8,
    image: 'https://via.placeholder.com/500x400?text=Adidas+Ultraboost+22',
    description: 'Adidas Ultraboost 22 là đỉnh cao của công nghệ giày chạy bộ với Boost midsole và Primeknit upper. Được thiết kế cho những vận động viên nghiêm túc.',
    category: 'shoes',
    brand: 'Adidas',
    rating: 4.9,
    reviews: 890,
    specifications: [
      { name: 'Thương hiệu', value: 'Adidas' },
      { name: 'Dòng sản phẩm', value: 'Ultraboost 22' },
      { name: 'Chất liệu', value: 'Primeknit + Boost' },
      { name: 'Đế giày', value: 'Boost midsole' },
      { name: 'Màu sắc', value: 'Core Black' },
      { name: 'Kích thước', value: '36-45' }
    ]
  }
])

const product = computed(() => {
  return products.value.find(p => p.id === parseInt(route.params.id))
})

onMounted(() => {
  // Simulate loading
  setTimeout(() => {
    loading.value = false
    if (product.value) {
      productImages.value = [
        product.value.image,
        'https://via.placeholder.com/500x400?text=iPhone+15+Pro+Back',
        'https://via.placeholder.com/500x400?text=iPhone+15+Pro+Side'
      ]
    }
  }, 1000)
})

const setMainImage = (imageUrl) => {
  if (product.value) {
    product.value.image = imageUrl
  }
}

const increaseQuantity = () => {
  quantity.value++
}

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--
  }
}

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price)
}

const addToCart = () => {
  if (product.value) {
    for (let i = 0; i < quantity.value; i++) {
      cartStore.addToCart(product.value)
    }
    // Có thể thêm thông báo thành công ở đây
  }
}

const buyNow = () => {
  addToCart()
  router.push('/cart')
}
</script>

<style scoped>
.product-detail {
  padding: 2rem 0;
}

.loading {
  text-align: center;
  padding: 3rem;
  color: #7f8c8d;
}

.product-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 3rem;
  width: 100%;
  margin: 0 auto;
  padding: 0 20px;
}

.product-images {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.main-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.thumbnail-images {
  display: flex;
  gap: 0.5rem;
}

.thumbnail {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  transition: opacity 0.3s;
  border: 2px solid transparent;
}

.thumbnail:hover {
  opacity: 0.8;
  border-color: #3498db;
}

.product-info {
  padding: 1rem 0;
}

.product-brand {
  font-size: 1rem;
  color: #3498db;
  font-weight: bold;
  margin-bottom: 0.5rem;
  text-transform: uppercase;
}

.product-title {
  color: #2c3e50;
  margin-bottom: 1rem;
  font-size: 2rem;
}

.product-rating {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.stars {
  color: #f39c12;
  font-size: 1.2rem;
}

.rating-text {
  color: #7f8c8d;
}

.product-price {
  margin-bottom: 2rem;
}

.current-price {
  font-size: 2rem;
  font-weight: bold;
  color: #e74c3c;
}

.original-price {
  font-size: 1.2rem;
  color: #7f8c8d;
  text-decoration: line-through;
  margin-left: 1rem;
}

.discount {
  background: #e74c3c;
  color: white;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.9rem;
  margin-left: 1rem;
}

.product-description,
.product-specs {
  margin-bottom: 2rem;
}

.product-description h3,
.product-specs h3 {
  color: #2c3e50;
  margin-bottom: 1rem;
}

.product-description p {
  color: #7f8c8d;
  line-height: 1.6;
}

.product-specs ul {
  list-style: none;
  padding: 0;
}

.product-specs li {
  padding: 0.5rem 0;
  border-bottom: 1px solid #ecf0f1;
  color: #2c3e50;
}

.product-actions {
  background: #f8f9fa;
  padding: 2rem;
  border-radius: 10px;
  margin-bottom: 2rem;
}

.quantity-selector {
  margin-bottom: 1.5rem;
}

.quantity-selector label {
  display: block;
  margin-bottom: 0.5rem;
  color: #2c3e50;
  font-weight: bold;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.quantity-btn {
  background: #3498db;
  color: white;
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1.2rem;
  font-weight: bold;
  transition: background 0.3s;
}

.quantity-btn:hover {
  background: #2980b9;
}

.quantity-input {
  width: 80px;
  height: 40px;
  text-align: center;
  border: 2px solid #ecf0f1;
  border-radius: 8px;
  font-size: 1rem;
}

.action-buttons {
  display: flex;
  gap: 1rem;
}

.add-to-cart-btn,
.buy-now-btn {
  flex: 1;
  padding: 1rem;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
}

.add-to-cart-btn {
  background: #3498db;
  color: white;
}

.add-to-cart-btn:hover {
  background: #2980b9;
}

.buy-now-btn {
  background: #e74c3c;
  color: white;
}

.buy-now-btn:hover {
  background: #c0392b;
}

.product-features {
  display: flex;
  gap: 2rem;
}

.feature {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #2c3e50;
}

.feature-icon {
  font-size: 1.2rem;
}

.not-found {
  text-align: center;
  padding: 3rem;
}

.not-found h2 {
  color: #2c3e50;
  margin-bottom: 1rem;
}

.back-to-products {
  background: #3498db;
  color: white;
  padding: 1rem 2rem;
  border-radius: 8px;
  text-decoration: none;
  font-weight: bold;
  transition: background 0.3s;
}

.back-to-products:hover {
  background: #2980b9;
}

@media (max-width: 768px) {
  .product-container {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .product-features {
    flex-direction: column;
    gap: 1rem;
  }
}
</style>
