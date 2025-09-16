<template>
  <div class="product-detail">
    <div v-if="loading" class="loading">
      <LoadingSpinner message="Đang tải sản phẩm..." />
    </div>
    
    <div v-else-if="error" class="error-container">
      <ErrorMessage :message="error" @retry="loadProduct" />
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
          <span v-if="product.originalPrice && product.originalPrice > product.price" class="original-price">
            {{ formatPrice(product.originalPrice) }}
          </span>
          <span v-if="product.isOnSale" class="discount">
            -{{ Math.round(product.discountPercentage) }}%
          </span>
        </div>
        
        <div class="product-description">
          <h3>Mô tả sản phẩm</h3>
          <p>{{ product.description }}</p>
        </div>
        
        <div class="product-specs">
          <h3>Thông số kỹ thuật</h3>
          <ul>
            <li v-for="spec in specifications" :key="spec.name">
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
import { productApi } from '../services/api.js'
import LoadingSpinner from '../components/LoadingSpinner.vue'
import ErrorMessage from '../components/ErrorMessage.vue'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const loading = ref(true)
const error = ref(null)
const quantity = ref(1)
const product = ref(null)
const productImages = ref([])

// Load product details from API
const loadProduct = async () => {
  try {
    loading.value = true
    error.value = null
    
    const response = await productApi.getProductById(route.params.id)
    
    if (response.result) {
      product.value = {
        id: response.result.id,
        name: response.result.name,
        price: response.result.price,
        originalPrice: response.result.originalPrice,
        discountPercentage: response.result.discountPercentage,
        isOnSale: response.result.isOnSale,
        description: response.result.description,
        brand: response.result.brand?.name || 'Unknown',
        category: response.result.category?.name || 'Unknown',
        color: response.result.color,
        size: response.result.size,
        material: response.result.material,
        gender: response.result.gender,
        viewCount: response.result.viewCount,
        soldCount: response.result.soldCount,
        rating: 4.5, // Mock rating for now
        reviews: Math.floor(Math.random() * 1000) + 100 // Mock reviews count
      }
      
      // Set up product images
      if (response.result.images && response.result.images.length > 0) {
        productImages.value = response.result.images.map(img => img.imageUrl)
        product.value.image = productImages.value[0]
      } else {
        productImages.value = ['https://via.placeholder.com/500x400?text=No+Image']
        product.value.image = productImages.value[0]
      }
    }
  } catch (err) {
    console.error('Error loading product:', err)
    error.value = 'Không thể tải thông tin sản phẩm'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadProduct()
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

// Computed properties for specifications
const specifications = computed(() => {
  if (!product.value) return []
  
  const specs = [
    { name: 'Thương hiệu', value: product.value.brand },
    { name: 'Danh mục', value: product.value.category }
  ]
  
  if (product.value.color) {
    specs.push({ name: 'Màu sắc', value: product.value.color })
  }
  
  if (product.value.size) {
    specs.push({ name: 'Kích thước', value: product.value.size })
  }
  
  if (product.value.material) {
    specs.push({ name: 'Chất liệu', value: product.value.material })
  }
  
  if (product.value.gender) {
    specs.push({ name: 'Giới tính', value: product.value.gender })
  }
  
  return specs
})
</script>

<style scoped>
.product-detail {
  padding: 2rem 0;
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
