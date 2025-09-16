<template>
  <div class="products">
    <div class="products-header">
      <h1>Danh sách sản phẩm</h1>
      
      <!-- Search and Filters -->
      <div class="search-filter">
        <input 
          v-model="searchQuery" 
          type="text" 
          placeholder="Tìm kiếm sản phẩm..."
          class="search-input"
        >
        
        <select v-model="selectedCategory" class="filter-select">
          <option value="">Tất cả danh mục</option>
          <option v-for="category in categories" :key="category.id" :value="category.id">
            {{ category.name }}
          </option>
        </select>
        
        <select v-model="selectedBrand" class="filter-select">
          <option value="">Tất cả thương hiệu</option>
          <option v-for="brand in brands" :key="brand.id" :value="brand.id">
            {{ brand.name }}
          </option>
        </select>
        
        <select v-model="selectedGender" class="filter-select">
          <option value="">Tất cả giới tính</option>
          <option value="Men">Nam</option>
          <option value="Women">Nữ</option>
          <option value="Unisex">Unisex</option>
          <option value="Kids">Trẻ em</option>
        </select>
        
        <div class="price-range">
          <input 
            v-model="minPrice" 
            type="number" 
            placeholder="Giá tối thiểu"
            class="price-input"
          >
          <span>-</span>
          <input 
            v-model="maxPrice" 
            type="number" 
            placeholder="Giá tối đa"
            class="price-input"
          >
        </div>
        
        <button @click="clearFilters" class="clear-filters-btn">
          Xóa bộ lọc
        </button>
      </div>
      
      <!-- Results info -->
      <div class="results-info">
        <p v-if="!loading">
          Hiển thị {{ filteredProducts.length }} sản phẩm 
          (trang {{ currentPage + 1 }} / {{ totalPages }})
        </p>
      </div>
    </div>

    <!-- Loading state -->
    <LoadingSpinner v-if="loading" message="Đang tải sản phẩm..." />
    
    <!-- Error state -->
    <ErrorMessage v-else-if="error" :message="error" @retry="loadProducts(currentPage)" />
    
    <!-- Products grid -->
    <div v-else-if="filteredProducts.length > 0" class="products-grid">
      <div v-for="product in filteredProducts" :key="product.id" class="product-card">
        <div class="product-image-container">
          <img :src="product.image" :alt="product.name" class="product-image">
          <div v-if="product.isOnSale" class="sale-badge">
            -{{ Math.round(product.discountPercentage) }}%
          </div>
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
          <div class="product-price-container">
            <span class="product-price">{{ formatPrice(product.price) }}</span>
            <span v-if="product.originalPrice && product.originalPrice > product.price" 
                  class="original-price">{{ formatPrice(product.originalPrice) }}</span>
          </div>
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

    <!-- No products found -->
    <div v-else class="no-products">
      <p>Không tìm thấy sản phẩm nào.</p>
      <button @click="clearFilters" class="clear-filters-btn">Xóa bộ lọc</button>
    </div>
    
    <!-- Pagination -->
    <div v-if="!loading && !error && totalPages > 1" class="pagination">
      <button 
        @click="prevPage" 
        :disabled="!hasPrevPage"
        class="pagination-btn"
      >
        Trước
      </button>
      
      <div class="pagination-info">
        Trang {{ currentPage + 1 }} / {{ totalPages }}
      </div>
      
      <button 
        @click="nextPage" 
        :disabled="!hasNextPage"
        class="pagination-btn"
      >
        Sau
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useCartStore } from '../stores/cart.js'
import { productApi, brandApi, categoryApi } from '../services/api.js'
import LoadingSpinner from '../components/LoadingSpinner.vue'
import ErrorMessage from '../components/ErrorMessage.vue'

const cartStore = useCartStore()
const searchQuery = ref('')
const selectedCategory = ref('')
const selectedBrand = ref('')
const minPrice = ref('')
const maxPrice = ref('')
const selectedGender = ref('')

const products = ref([])
const brands = ref([])
const categories = ref([])
const loading = ref(true)
const error = ref(null)
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)

// Load brands and categories for filters
const loadBrandsAndCategories = async () => {
  try {
    const [brandsResponse, categoriesResponse] = await Promise.all([
      brandApi.getActiveBrands(),
      categoryApi.getActiveCategories()
    ])
    
    brands.value = brandsResponse.result || []
    categories.value = categoriesResponse.result || []
  } catch (err) {
    console.error('Error loading brands and categories:', err)
  }
}

// Load products with filters
const loadProducts = async (page = 0) => {
  try {
    loading.value = true
    error.value = null
    
    let response
    
    if (searchQuery.value.trim()) {
      // Search products
      response = await productApi.searchProducts(searchQuery.value.trim(), page, 20)
    } else {
      // Filter products
      const filters = {}
      if (selectedCategory.value) filters.categoryId = selectedCategory.value
      if (selectedBrand.value) filters.brandId = selectedBrand.value
      if (minPrice.value) filters.minPrice = parseFloat(minPrice.value)
      if (maxPrice.value) filters.maxPrice = parseFloat(maxPrice.value)
      if (selectedGender.value) filters.gender = selectedGender.value
      
      response = await productApi.filterProducts(filters, page, 20)
    }
    
    if (response.result) {
      products.value = response.result.content || []
      currentPage.value = response.result.number || 0
      totalPages.value = response.result.totalPages || 0
      totalElements.value = response.result.totalElements || 0
    }
  } catch (err) {
    console.error('Error loading products:', err)
    error.value = 'Không thể tải danh sách sản phẩm'
    products.value = []
  } finally {
    loading.value = false
  }
}

// Computed properties
const filteredProducts = computed(() => {
  return products.value.map(product => ({
    id: product.id,
    name: product.name,
    price: product.price,
    originalPrice: product.originalPrice,
    discountPercentage: product.discountPercentage,
    isOnSale: product.isOnSale,
    image: product.images && product.images.length > 0 
      ? product.images[0].imageUrl 
      : 'https://via.placeholder.com/300x200?text=No+Image',
    description: product.description,
    category: product.category?.name || 'Unknown',
    brand: product.brand?.name || 'Unknown',
    gender: product.gender,
    rating: 4.5 // Mock rating for now
  }))
})

const hasNextPage = computed(() => currentPage.value < totalPages.value - 1)
const hasPrevPage = computed(() => currentPage.value > 0)

// Methods
const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price)
}

const addToCart = (product) => {
  cartStore.addToCart(product)
}

const clearFilters = () => {
  searchQuery.value = ''
  selectedCategory.value = ''
  selectedBrand.value = ''
  minPrice.value = ''
  maxPrice.value = ''
  selectedGender.value = ''
  loadProducts(0)
}

const nextPage = () => {
  if (hasNextPage.value) {
    loadProducts(currentPage.value + 1)
  }
}

const prevPage = () => {
  if (hasPrevPage.value) {
    loadProducts(currentPage.value - 1)
  }
}

// Watchers
watch([searchQuery, selectedCategory, selectedBrand, minPrice, maxPrice, selectedGender], () => {
  loadProducts(0)
}, { deep: true })

// Lifecycle
onMounted(() => {
  loadBrandsAndCategories()
  
  // Handle URL query parameters
  const urlParams = new URLSearchParams(window.location.search)
  if (urlParams.get('brandId')) {
    selectedBrand.value = urlParams.get('brandId')
  }
  if (urlParams.get('categoryId')) {
    selectedCategory.value = urlParams.get('categoryId')
  }
  if (urlParams.get('search')) {
    searchQuery.value = urlParams.get('search')
  }
  
  loadProducts(0)
})
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
.filter-select,
.price-input {
  padding: 0.75rem;
  border: 2px solid #ecf0f1;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.search-input {
  flex: 2;
}

.filter-select {
  flex: 1;
}

.price-range {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex: 1;
}

.price-input {
  flex: 1;
  min-width: 100px;
}

.clear-filters-btn {
  background: #e74c3c;
  color: white;
  border: none;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  transition: background 0.3s;
}

.clear-filters-btn:hover {
  background: #c0392b;
}

.search-input:focus,
.filter-select:focus,
.price-input:focus {
  outline: none;
  border-color: #3498db;
}

.results-info {
  text-align: center;
  margin: 1rem 0;
  color: #7f8c8d;
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

.product-price-container {
  margin-bottom: 0.5rem;
}

.product-price {
  font-size: 1.2rem;
  font-weight: bold;
  color: #e74c3c;
}

.original-price {
  font-size: 1rem;
  color: #7f8c8d;
  text-decoration: line-through;
  margin-left: 0.5rem;
}

.sale-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: #e74c3c;
  color: white;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: bold;
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

.no-products .clear-filters-btn {
  margin-top: 1rem;
}


/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin: 2rem 0;
}

.pagination-btn {
  background: #3498db;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
  transition: background 0.3s;
}

.pagination-btn:hover:not(:disabled) {
  background: #2980b9;
}

.pagination-btn:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
}

.pagination-info {
  color: #7f8c8d;
  font-weight: bold;
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
    gap: 0.5rem;
  }
  
  .price-range {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .price-input {
    min-width: auto;
  }
  
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 1rem;
  }
  
  .pagination {
    flex-direction: column;
    gap: 0.5rem;
  }
}
</style>
