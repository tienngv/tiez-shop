<template>
  <div class="callback">
    <div class="callback-container">
      <div class="callback-content">
        <div v-if="loading" class="loading">
          <div class="spinner"></div>
          <p>Đang xử lý đăng nhập...</p>
        </div>
        
        <div v-else-if="error" class="error">
          <h2>❌ Đăng nhập thất bại</h2>
          <p>{{ error }}</p>
          <button @click="goToLogin" class="retry-btn">
            Thử lại
          </button>
        </div>
        
        <div v-else class="success">
          <h2>✅ Đăng nhập thành công!</h2>
          <p>Đang chuyển hướng...</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const loading = ref(true)
const error = ref(null)

onMounted(async () => {
  try {
    // Lấy authorization code từ URL
    const code = route.query.code
    const state = route.query.state
    
    if (!code) {
      throw new Error('Không có authorization code từ Keycloak')
    }
    
    console.log('Received authorization code:', code)
    
    // Exchange authorization code để lấy token
    const tokenResponse = await exchangeCodeForToken(code)
    
    if (tokenResponse.access_token) {
      // Lưu token vào localStorage
      localStorage.setItem('access_token', tokenResponse.access_token)
      localStorage.setItem('refresh_token', tokenResponse.refresh_token)
      localStorage.setItem('token_type', tokenResponse.token_type)
      localStorage.setItem('expires_in', tokenResponse.expires_in.toString())
      
      // Cập nhật auth store
      authStore.setToken(tokenResponse.access_token)
      
      // Lấy thông tin user từ token
      const userInfo = await getUserInfo(tokenResponse.access_token)
      
      if (userInfo) {
        authStore.setUser(userInfo)
        console.log('Login successful, redirecting to home')
        
        // Redirect về trang chủ
        setTimeout(() => {
          router.push('/')
        }, 1000)
      } else {
        throw new Error('Không thể lấy thông tin user')
      }
    } else {
      throw new Error('Không thể lấy access token')
    }
  } catch (err) {
    console.error('Callback error:', err)
    error.value = err.message
    loading.value = false
  }
})

// Exchange authorization code để lấy token
const exchangeCodeForToken = async (code) => {
  const tokenUrl = 'http://localhost:8180/realms/tiez-shop/protocol/openid-connect/token'
  
  const params = new URLSearchParams({
    grant_type: 'authorization_code',
    client_id: 'tienngv',
    client_secret: '6eRqOoKNobwIoRTwLWg3JX3uz7Opl6hc', // Nên lưu trong env
    code: code,
    redirect_uri: 'http://localhost:5173/callback'
  })
  
  const response = await fetch(tokenUrl, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    body: params
  })
  
  if (!response.ok) {
    const errorText = await response.text()
    throw new Error(`Token exchange failed: ${errorText}`)
  }
  
  return await response.json()
}

// Lấy thông tin user từ token
const getUserInfo = async (accessToken) => {
  const userInfoUrl = 'http://localhost:8180/realms/tiez-shop/protocol/openid-connect/userinfo'
  
  const response = await fetch(userInfoUrl, {
    headers: {
      'Authorization': `Bearer ${accessToken}`
    }
  })
  
  if (!response.ok) {
    throw new Error('Failed to get user info')
  }
  
  return await response.json()
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.callback {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem;
}

.callback-container {
  background: white;
  border-radius: 15px;
  box-shadow: 0 20px 40px rgba(0,0,0,0.1);
  overflow: hidden;
  max-width: 500px;
  width: 100%;
}

.callback-content {
  padding: 3rem;
  text-align: center;
}

.loading {
  padding: 2rem;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading p {
  color: #3498db;
  font-size: 1.1rem;
}

.error h2 {
  color: #e74c3c;
  margin-bottom: 1rem;
}

.error p {
  color: #7f8c8d;
  margin-bottom: 2rem;
}

.success h2 {
  color: #27ae60;
  margin-bottom: 1rem;
}

.success p {
  color: #7f8c8d;
}

.retry-btn {
  background: #e74c3c;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.3s;
}

.retry-btn:hover {
  background: #c0392b;
}
</style>
