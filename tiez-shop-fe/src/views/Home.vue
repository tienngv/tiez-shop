<template>
  <div class="home">
    <div class="hero-section">
      <div class="hero-content">
        <h1>Chào mừng đến với TiezShop</h1>
        <p>Khám phá thế giới thời trang với các thương hiệu nổi tiếng: Nike, Adidas, Puma và nhiều hơn nữa</p>
        <router-link to="/products" class="cta-button">
          Khám phá ngay
        </router-link>
      </div>
    </div>

    <div class="features-section">
      <div class="container">
        <h2>Tại sao chọn TiezShop?</h2>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">👟</div>
            <h3>Thương hiệu chính hãng</h3>
            <p>100% sản phẩm chính hãng từ Nike, Adidas, Puma và các thương hiệu nổi tiếng</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">🚚</div>
            <h3>Giao hàng miễn phí</h3>
            <p>Miễn phí vận chuyển cho đơn hàng từ 500k, giao hàng trong 24h</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">💳</div>
            <h3>Thanh toán linh hoạt</h3>
            <p>Hỗ trợ nhiều phương thức thanh toán và trả góp 0% lãi suất</p>
          </div>
        </div>
      </div>
    </div>

    <div class="products-preview">
      <div class="container">
        <h2>Sản phẩm nổi bật</h2>
        <div class="products-grid">
          <div v-for="product in featuredProducts" :key="product.id" class="product-card">
            <img :src="product.image" :alt="product.name" class="product-image">
            <div class="product-info">
              <div class="product-brand">{{ product.brand }}</div>
              <h3>{{ product.name }}</h3>
              <p class="product-price">{{ formatPrice(product.price) }}</p>
              <button @click="addToCart(product)" class="add-to-cart-btn">
                Thêm vào giỏ
              </button>
            </div>
          </div>
        </div>
        <div class="view-all">
          <router-link to="/products" class="view-all-btn">
            Xem tất cả sản phẩm
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useCartStore } from '../stores/cart.js'

const cartStore = useCartStore()

// Mock data - trong thực tế sẽ lấy từ API
const featuredProducts = ref([
  {
    id: 1,
    name: 'Nike Air Force 1',
    price: 2890000,
    image: 'https://via.placeholder.com/300x200?text=Nike+Air+Force+1',
    brand: 'Nike',
    category: 'shoes'
  },
  {
    id: 2,
    name: 'Adidas Ultraboost 22',
    price: 4590000,
    image: 'https://via.placeholder.com/300x200?text=Adidas+Ultraboost+22',
    brand: 'Adidas',
    category: 'shoes'
  },
  {
    id: 3,
    name: 'Nike Tech Fleece Hoodie',
    price: 1890000,
    image: 'https://via.placeholder.com/300x200?text=Nike+Tech+Fleece',
    brand: 'Nike',
    category: 'clothing'
  },
  {
    id: 4,
    name: 'Adidas Originals Trefoil Tee',
    price: 890000,
    image: 'https://via.placeholder.com/300x200?text=Adidas+Trefoil+Tee',
    brand: 'Adidas',
    category: 'clothing'
  }
])

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price)
}

const addToCart = (product) => {
  cartStore.addToCart(product)
  // Có thể thêm thông báo thành công ở đây
}
</script>

<style scoped>
.home {
  min-height: 100vh;
}

.container {
  width: 100%;
  margin: 0 auto;
  padding: 0 20px;
}

/* Hero Section */
.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4rem 0;
  text-align: center;
}

.hero-content h1 {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.hero-content p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

.cta-button {
  background: white;
  color: #667eea;
  padding: 1rem 2rem;
  border-radius: 50px;
  text-decoration: none;
  font-weight: bold;
  font-size: 1.1rem;
  transition: transform 0.3s, box-shadow 0.3s;
}

.cta-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.2);
}

/* Features Section */
.features-section {
  padding: 4rem 0;
  background: #f8f9fa;
}

.features-section h2 {
  text-align: center;
  margin-bottom: 3rem;
  color: #2c3e50;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  max-width: 1400px;
  margin: 0 auto;
}

.feature-card {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  transition: transform 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.feature-card h3 {
  color: #2c3e50;
  margin-bottom: 1rem;
}

/* Products Preview */
.products-preview {
  padding: 4rem 0;
}

.products-preview h2 {
  text-align: center;
  margin-bottom: 3rem;
  color: #2c3e50;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
  max-width: 1400px;
  margin-left: auto;
  margin-right: auto;
}

.product-card {
  background: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.product-info {
  padding: 1.5rem;
}

.product-brand {
  font-size: 0.9rem;
  color: #3498db;
  font-weight: bold;
  margin-bottom: 0.5rem;
  text-transform: uppercase;
}

.product-info h3 {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 1rem;
}

.product-price {
  font-size: 1.2rem;
  font-weight: bold;
  color: #e74c3c;
  margin-bottom: 1rem;
}

.add-to-cart-btn {
  background: #3498db;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 5px;
  cursor: pointer;
  width: 100%;
  font-weight: bold;
  transition: background 0.3s;
}

.add-to-cart-btn:hover {
  background: #2980b9;
}

.view-all {
  text-align: center;
}

.view-all-btn {
  background: #2c3e50;
  color: white;
  padding: 1rem 2rem;
  border-radius: 50px;
  text-decoration: none;
  font-weight: bold;
  transition: background 0.3s;
}

.view-all-btn:hover {
  background: #34495e;
}

@media (min-width: 1920px) {
  .features-grid {
    grid-template-columns: repeat(4, 1fr);
  }
  
  .products-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (min-width: 2560px) {
  .features-grid {
    grid-template-columns: repeat(5, 1fr);
  }
  
  .products-grid {
    grid-template-columns: repeat(5, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-content h1 {
    font-size: 2rem;
  }
  
  .features-grid,
  .products-grid {
    grid-template-columns: 1fr;
  }
}
</style>
