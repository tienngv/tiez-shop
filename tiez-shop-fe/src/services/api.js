import axios from 'axios'

// Cấu hình base URL cho API
const API_BASE_URL = 'http://localhost:8080'

// Tạo axios instance với cấu hình mặc định
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Interceptor để thêm token vào header
apiClient.interceptors.request.use(
  (config) => {
    // Lấy token từ localStorage hoặc auth store
    const token = localStorage.getItem('access_token')
    console.log('API Request:', {
      url: config.url,
      method: config.method,
      hasToken: !!token,
      tokenPreview: token ? token.substring(0, 20) + '...' : 'No token'
    })
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    } else {
      console.warn('No access token found for API request')
    }
    return config
  },
  (error) => {
    console.error('Request interceptor error:', error)
    return Promise.reject(error)
  }
)

// Interceptor để xử lý response
apiClient.interceptors.response.use(
  (response) => {
    console.log('API Response:', {
      url: response.config.url,
      status: response.status,
      data: response.data
    })
    return response
  },
  async (error) => {
    console.error('API Error:', {
      url: error.config?.url,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      message: error.message
    })
    
    // Xử lý lỗi 401 (Unauthorized)
    if (error.response?.status === 401) {
      console.error('401 Unauthorized - Attempting token refresh')
      
      try {
        // Try to refresh token
        const { refreshToken } = await import('./keycloak.js')
        const refreshed = await refreshToken()
        
        if (refreshed) {
          console.log('Token refreshed, retrying request')
          // Retry the original request with new token
          const newToken = localStorage.getItem('access_token')
          if (newToken) {
            error.config.headers.Authorization = `Bearer ${newToken}`
            return apiClient.request(error.config)
          }
        }
      } catch (refreshError) {
        console.error('Token refresh failed:', refreshError)
      }
      
      // If refresh failed or no token, redirect to login
      localStorage.removeItem('access_token')
      localStorage.removeItem('refresh_token')
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

// API functions
export const userApi = {
  // Đăng ký user
  register: async (userData) => {
    try {
      const response = await apiClient.post('/api/user/register', userData)
      return response.data
    } catch (error) {
      console.error('Error registering user:', error)
      throw error
    }
  },

  // Đăng nhập
  login: async (credentials) => {
    try {
      const response = await apiClient.post('/api/user/login', credentials)
      return response.data
    } catch (error) {
      console.error('Error logging in:', error)
      throw error
    }
  },

  // Lấy thông tin user theo ID
  getUserById: async (userId) => {
    try {
      const response = await apiClient.get(`/api/user/${userId}`)
      return response.data
    } catch (error) {
      console.error('Error fetching user:', error)
      throw error
    }
  },

  // Cập nhật thông tin user
  updateUser: async (userId, userData) => {
    try {
      const response = await apiClient.put(`/api/user/${userId}`, userData)
      return response.data
    } catch (error) {
      console.error('Error updating user:', error)
      throw error
    }
  },

  // Đổi mật khẩu
  changePassword: async (passwordData) => {
    try {
      const response = await apiClient.post('/api/user/change-pass', passwordData)
      return response.data
    } catch (error) {
      console.error('Error changing password:', error)
      throw error
    }
  }
}

export const productApi = {
  // Lấy tất cả sản phẩm có phân trang
  getAllProducts: async (page = 0, size = 20) => {
    try {
      const response = await apiClient.get(`/api/products?page=${page}&size=${size}`)
      return response.data
    } catch (error) {
      console.error('Error fetching products:', error)
      throw error
    }
  },

  // Lấy sản phẩm theo ID
  getProductById: async (productId) => {
    try {
      const response = await apiClient.get(`/api/products/${productId}`)
      return response.data
    } catch (error) {
      console.error('Error fetching product:', error)
      throw error
    }
  },

  // Lấy sản phẩm nổi bật
  getFeaturedProducts: async (page = 0, size = 20) => {
    try {
      const response = await apiClient.get(`/api/products/featured?page=${page}&size=${size}`)
      return response.data
    } catch (error) {
      console.error('Error fetching featured products:', error)
      throw error
    }
  },

  // Tìm kiếm sản phẩm
  searchProducts: async (keyword, page = 0, size = 20) => {
    try {
      const response = await apiClient.get(`/api/products/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`)
      return response.data
    } catch (error) {
      console.error('Error searching products:', error)
      throw error
    }
  },

  // Lọc sản phẩm
  filterProducts: async (filters = {}, page = 0, size = 20) => {
    try {
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        ...filters
      })
      const response = await apiClient.get(`/api/products/filter?${params}`)
      return response.data
    } catch (error) {
      console.error('Error filtering products:', error)
      throw error
    }
  },

  // Lấy sản phẩm đang giảm giá
  getProductsOnSale: async (page = 0, size = 20) => {
    try {
      const response = await apiClient.get(`/api/products/on-sale?page=${page}&size=${size}`)
      return response.data
    } catch (error) {
      console.error('Error fetching products on sale:', error)
      throw error
    }
  },

  // Lấy sản phẩm xem nhiều nhất
  getTopViewedProducts: async (limit = 10) => {
    try {
      const response = await apiClient.get(`/api/products/top-viewed?limit=${limit}`)
      return response.data
    } catch (error) {
      console.error('Error fetching top viewed products:', error)
      throw error
    }
  },

  // Lấy sản phẩm bán chạy nhất
  getTopSellingProducts: async (limit = 10) => {
    try {
      const response = await apiClient.get(`/api/products/top-selling?limit=${limit}`)
      return response.data
    } catch (error) {
      console.error('Error fetching top selling products:', error)
      throw error
    }
  }
}

export const brandApi = {
  // Lấy tất cả thương hiệu có phân trang
  getAllBrands: async (page = 0, size = 20) => {
    try {
      const response = await apiClient.get(`/api/brands?page=${page}&size=${size}`)
      return response.data
    } catch (error) {
      console.error('Error fetching brands:', error)
      throw error
    }
  },

  // Lấy thương hiệu theo ID
  getBrandById: async (brandId) => {
    try {
      const response = await apiClient.get(`/api/brands/${brandId}`)
      return response.data
    } catch (error) {
      console.error('Error fetching brand:', error)
      throw error
    }
  },

  // Lấy danh sách thương hiệu đang hoạt động
  getActiveBrands: async () => {
    try {
      const response = await apiClient.get('/api/brands/active')
      return response.data
    } catch (error) {
      console.error('Error fetching active brands:', error)
      throw error
    }
  },

  // Tìm kiếm thương hiệu
  searchBrands: async (name) => {
    try {
      const response = await apiClient.get(`/api/brands/search?name=${encodeURIComponent(name)}`)
      return response.data
    } catch (error) {
      console.error('Error searching brands:', error)
      throw error
    }
  },

  // Lấy số lượng sản phẩm theo thương hiệu
  getProductCountByBrand: async (brandId) => {
    try {
      const response = await apiClient.get(`/api/brands/${brandId}/product-count`)
      return response.data
    } catch (error) {
      console.error('Error fetching product count by brand:', error)
      throw error
    }
  }
}

export const categoryApi = {
  // Lấy tất cả danh mục có phân trang
  getAllCategories: async (page = 0, size = 20) => {
    try {
      const response = await apiClient.get(`/api/categories?page=${page}&size=${size}`)
      return response.data
    } catch (error) {
      console.error('Error fetching categories:', error)
      throw error
    }
  },

  // Lấy danh mục theo ID
  getCategoryById: async (categoryId) => {
    try {
      const response = await apiClient.get(`/api/categories/${categoryId}`)
      return response.data
    } catch (error) {
      console.error('Error fetching category:', error)
      throw error
    }
  },

  // Lấy danh sách danh mục đang hoạt động
  getActiveCategories: async () => {
    try {
      const response = await apiClient.get('/api/categories/active')
      return response.data
    } catch (error) {
      console.error('Error fetching active categories:', error)
      throw error
    }
  },

  // Lấy danh mục gốc
  getRootCategories: async () => {
    try {
      const response = await apiClient.get('/api/categories/root')
      return response.data
    } catch (error) {
      console.error('Error fetching root categories:', error)
      throw error
    }
  },

  // Lấy danh mục con
  getSubCategories: async (categoryId) => {
    try {
      const response = await apiClient.get(`/api/categories/${categoryId}/subcategories`)
      return response.data
    } catch (error) {
      console.error('Error fetching subcategories:', error)
      throw error
    }
  },

  // Tìm kiếm danh mục
  searchCategories: async (name) => {
    try {
      const response = await apiClient.get(`/api/categories/search?name=${encodeURIComponent(name)}`)
      return response.data
    } catch (error) {
      console.error('Error searching categories:', error)
      throw error
    }
  },

  // Lấy số lượng sản phẩm theo danh mục
  getProductCountByCategory: async (categoryId) => {
    try {
      const response = await apiClient.get(`/api/categories/${categoryId}/product-count`)
      return response.data
    } catch (error) {
      console.error('Error fetching product count by category:', error)
      throw error
    }
  }
}

export default apiClient
