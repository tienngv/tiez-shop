<template>
  <div class="products">
    <div class="products-header">
      <h1>Danh sách sản phẩm</h1>
      <div class="search-filter">
        <input 
          v-model="searchQuery" 
          type="text" 
          placeholder="Tìm kiếm sản phẩm..."
          class="search-input"
        >
        <select v-model="selectedCategory" class="category-select">
          <option value="">Tất cả danh mục</option>
          <option value="shoes">Giày dép</option>
          <option value="clothing">Quần áo</option>
          <option value="accessories">Phụ kiện</option>
          <option value="bags">Túi xách</option>
        </select>
        
        <select v-model="selectedBrand" class="brand-select">
          <option value="">Tất cả thương hiệu</option>
          <option value="nike">Nike</option>
          <option value="adidas">Adidas</option>
          <option value="puma">Puma</option>
          <option value="converse">Converse</option>
          <option value="vans">Vans</option>
        </select>
      </div>
    </div>

    <div class="products-grid">
      <div v-for="product in filteredProducts" :key="product.id" class="product-card">
        <div class="product-image-container">
          <img :src="product.image" :alt="product.name" class="product-image">
          <div class="product-overlay">
            <router-link :to="`/product/${product.id}`" class="view-details-btn">
              Xem chi tiết
            </router-link>
          </div>
        </div>
          <div class="product-info">
            <div class="product-brand">{{ product.brand }}</div>
            <h3 class="product-name">{{ product.name }}</h3>
            <p class="product-description">{{ product.description }}</p>
            <div class="product-price">{{ formatPrice(product.price) }}</div>
            <div class="product-rating">
              <span class="stars">★★★★☆</span>
              <span class="rating-text">({{ product.rating }})</span>
            </div>
            <button @click="addToCart(product)" class="add-to-cart-btn">
              Thêm vào giỏ
            </button>
          </div>
      </div>
    </div>

    <div v-if="filteredProducts.length === 0" class="no-products">
      <p>Không tìm thấy sản phẩm nào.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useCartStore } from '../stores/cart.js'

const cartStore = useCartStore()
const searchQuery = ref('')
const selectedCategory = ref('')
const selectedBrand = ref('')

// Mock data - trong thực tế sẽ lấy từ API
const products = ref([
  {
    id: 1,
    name: 'Nike Air Force 1',
    price: 2890000,
    image: 'https://via.placeholder.com/300x200?text=Nike+Air+Force+1',
    description: 'Giày thể thao Nike Air Force 1 cổ điển, thiết kế đơn giản và thời trang',
    category: 'shoes',
    brand: 'Nike',
    rating: 4.8
  },
  {
    id: 2,
    name: 'Adidas Ultraboost 22',
    price: 4590000,
    image: 'https://via.placeholder.com/300x200?text=Adidas+Ultraboost+22',
    description: 'Giày chạy bộ Adidas Ultraboost với công nghệ Boost tiên tiến',
    category: 'shoes',
    brand: 'Adidas',
    rating: 4.9
  },
  {
    id: 3,
    name: 'Nike Tech Fleece Hoodie',
    price: 1890000,
    image: 'https://via.placeholder.com/300x200?text=Nike+Tech+Fleece',
    description: 'Áo hoodie Nike Tech Fleece ấm áp và thoải mái',
    category: 'clothing',
    brand: 'Nike',
    rating: 4.7
  },
  {
    id: 4,
    name: 'Adidas Originals Trefoil Tee',
    price: 890000,
    image: 'https://via.placeholder.com/300x200?text=Adidas+Trefoil+Tee',
    description: 'Áo thun Adidas Originals với logo Trefoil cổ điển',
    category: 'clothing',
    brand: 'Adidas',
    rating: 4.6
  },
  {
    id: 5,
    name: 'Converse Chuck Taylor All Star',
    price: 1590000,
    image: 'https://via.placeholder.com/300x200?text=Converse+Chuck+Taylor',
    description: 'Giày Converse Chuck Taylor All Star phong cách cổ điển',
    category: 'shoes',
    brand: 'Converse',
    rating: 4.5
  },
  {
    id: 6,
    name: 'Puma RS-X Reinvention',
    price: 2290000,
    image: 'https://via.placeholder.com/300x200?text=Puma+RS-X',
    description: 'Giày Puma RS-X với thiết kế retro-futuristic độc đáo',
    category: 'shoes',
    brand: 'Puma',
    rating: 4.4
  },
  {
    id: 7,
    name: 'Vans Old Skool',
    price: 1790000,
    image: 'https://via.placeholder.com/300x200?text=Vans+Old+Skool',
    description: 'Giày Vans Old Skool phong cách skateboard cổ điển',
    category: 'shoes',
    brand: 'Vans',
    rating: 4.6
  },
  {
    id: 8,
    name: 'Nike Dri-FIT Training Shorts',
    price: 1290000,
    image: 'https://via.placeholder.com/300x200?text=Nike+Dri-FIT+Shorts',
    description: 'Quần short Nike Dri-FIT cho tập luyện thoải mái',
    category: 'clothing',
    brand: 'Nike',
    rating: 4.3
  },
  {
    id: 9,
    name: 'Adidas Backpack',
    price: 1590000,
    image: 'https://via.placeholder.com/300x200?text=Adidas+Backpack',
    description: 'Ba lô Adidas với thiết kế thể thao và nhiều ngăn',
    category: 'bags',
    brand: 'Adidas',
    rating: 4.5
  },
  {
    id: 10,
    name: 'Nike Cap',
    price: 690000,
    image: 'https://via.placeholder.com/300x200?text=Nike+Cap',
    description: 'Mũ lưỡi trai Nike với logo Swoosh nổi bật',
    category: 'accessories',
    brand: 'Nike',
    rating: 4.2
  }
])

const filteredProducts = computed(() => {
  let filtered = products.value

  // Filter by search query
  if (searchQuery.value) {
    filtered = filtered.filter(product =>
      product.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      product.description.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      product.brand.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }

  // Filter by category
  if (selectedCategory.value) {
    filtered = filtered.filter(product => product.category === selectedCategory.value)
  }

  // Filter by brand
  if (selectedBrand.value) {
    filtered = filtered.filter(product => product.brand.toLowerCase() === selectedBrand.value.toLowerCase())
  }

  return filtered
})

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
.products {
  padding: 2rem 0;
  width: 100%;
}

.products-header {
  margin-bottom: 2rem;
}

.products-header h1 {
  color: #2c3e50;
  margin-bottom: 1.5rem;
  text-align: center;
}

.search-filter {
  display: flex;
  gap: 1rem;
  max-width: 800px;
  margin: 0 auto;
}

.search-input,
.category-select,
.brand-select {
  flex: 1;
  padding: 0.75rem;
  border: 2px solid #ecf0f1;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.search-input:focus,
.category-select:focus,
.brand-select:focus {
  outline: none;
  border-color: #3498db;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 2rem;
  max-width: 1400px;
  margin: 0 auto;
}

.product-card {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0,0,0,0.15);
}

.product-image-container {
  position: relative;
  overflow: hidden;
}

.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  transition: transform 0.3s;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

.product-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.product-card:hover .product-overlay {
  opacity: 1;
}

.view-details-btn {
  background: white;
  color: #2c3e50;
  padding: 0.75rem 1.5rem;
  border-radius: 25px;
  text-decoration: none;
  font-weight: bold;
  transition: transform 0.3s;
}

.view-details-btn:hover {
  transform: scale(1.05);
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

.product-name {
  color: #2c3e50;
  margin: 0 0 0.5rem 0;
  font-size: 1.1rem;
}

.product-description {
  color: #7f8c8d;
  font-size: 0.9rem;
  margin-bottom: 1rem;
  line-height: 1.4;
}

.product-price {
  font-size: 1.2rem;
  font-weight: bold;
  color: #e74c3c;
  margin-bottom: 0.5rem;
}

.product-rating {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.stars {
  color: #f39c12;
  font-size: 1rem;
}

.rating-text {
  color: #7f8c8d;
  font-size: 0.9rem;
}

.add-to-cart-btn {
  background: #3498db;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  cursor: pointer;
  width: 100%;
  font-weight: bold;
  transition: background 0.3s;
}

.add-to-cart-btn:hover {
  background: #2980b9;
}

.no-products {
  text-align: center;
  padding: 3rem;
  color: #7f8c8d;
}

@media (min-width: 1920px) {
  .products-grid {
    grid-template-columns: repeat(5, 1fr);
  }
}

@media (min-width: 2560px) {
  .products-grid {
    grid-template-columns: repeat(6, 1fr);
  }
}

@media (max-width: 768px) {
  .search-filter {
    flex-direction: column;
  }
  
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 1rem;
  }
}
</style>
