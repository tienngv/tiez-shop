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
  // Lấy thông tin user theo ID
  getUserById: async (userId) => {
    try {
      const response = await apiClient.get(`/user/${userId}`)
      return response.data
    } catch (error) {
      console.error('Error fetching user:', error)
      throw error
    }
  },

  // Cập nhật thông tin user
  updateUser: async (userId, userData) => {
    try {
      const response = await apiClient.put(`/user/${userId}`, userData)
      return response.data
    } catch (error) {
      console.error('Error updating user:', error)
      throw error
    }
  },

  // Lấy danh sách đơn hàng của user
  getUserOrders: async (userId) => {
    try {
      const response = await apiClient.get(`/user/${userId}/orders`)
      return response.data
    } catch (error) {
      console.error('Error fetching user orders:', error)
      throw error
    }
  }
}

export default apiClient
