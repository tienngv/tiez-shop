<template>
  <div class="logout">
    <div class="logout-container">
      <div class="logout-content">
        <div v-if="loading" class="loading">
          <div class="spinner"></div>
          <p>Đang đăng xuất...</p>
        </div>
        
        <div v-else-if="error" class="error">
          <h2>❌ Đăng xuất thất bại</h2>
          <p>{{ error }}</p>
          <button @click="goToHome" class="retry-btn">
            Về trang chủ
          </button>
        </div>
        
        <div v-else class="success">
          <h2>✅ Đăng xuất thành công!</h2>
          <p>Đang chuyển hướng...</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(true)
const error = ref(null)

onMounted(async () => {
  try {
    console.log('Starting logout process...')
    
    // Clear local tokens and state
    authStore.logout()
    
    // Redirect to Keycloak logout
    const logoutUrl = `http://localhost:8180/realms/tiez-shop/protocol/openid-connect/logout?` +
      `client_id=tienngv&` +
      `post_logout_redirect_uri=${encodeURIComponent('http://localhost:5173')}`
    
    console.log('Redirecting to Keycloak logout:', logoutUrl)
    
    // Redirect to Keycloak logout
    window.location.href = logoutUrl
    
  } catch (err) {
    console.error('Logout error:', err)
    error.value = err.message
    loading.value = false
  }
})

const goToHome = () => {
  router.push('/')
}
</script>

<style scoped>
.logout {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem;
}

.logout-container {
  background: white;
  border-radius: 15px;
  box-shadow: 0 20px 40px rgba(0,0,0,0.1);
  overflow: hidden;
  max-width: 500px;
  width: 100%;
}

.logout-content {
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
