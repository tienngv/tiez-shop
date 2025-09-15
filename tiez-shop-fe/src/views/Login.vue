<template>
  <div class="login">
    <div class="login-container">
      <div class="login-form">
        <h1>Đăng nhập</h1>
        <p>Đăng nhập để truy cập vào tài khoản của bạn</p>
        
        <div v-if="loading" class="loading">
          <p>Đang xử lý...</p>
        </div>
        
        <div v-else class="login-options">
          <button @click="handleKeycloakLogin" class="keycloak-login-btn">
            <i class="login-icon">🔐</i>
            Đăng nhập với Keycloak
          </button>
          
          <div class="divider">
            <span>hoặc</span>
          </div>
          
          <div class="demo-login">
            <p>Demo với tài khoản mẫu:</p>
            <button @click="handleDemoLogin" class="demo-login-btn">
              Đăng nhập Demo
            </button>
          </div>
        </div>
        
        <div class="login-info">
          <h3>Thông tin tài khoản demo:</h3>
          <ul>
            <li><strong>Username:</strong> demo@tiezshop.com</li>
            <li><strong>Password:</strong> demo123</li>
            <li><strong>Role:</strong> Customer</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'
import { login } from '../services/keycloak.js'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)

const handleKeycloakLogin = async () => {
  loading.value = true
  try {
    // Clear any existing tokens before login
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
    console.log('Cleared existing tokens before login')
    
    await login()
  } catch (error) {
    console.error('Login failed:', error)
    
    // Kiểm tra loại lỗi cụ thể
    if (error.error === 'invalid_request' && error.error_description?.includes('redirect_uri')) {
      alert('Lỗi cấu hình Keycloak: redirect_uri không hợp lệ. Vui lòng kiểm tra cấu hình client trong Keycloak Admin Console.')
    } else if (error.error === 'invalid_client') {
      alert('Client ID không tồn tại hoặc không đúng. Vui lòng kiểm tra cấu hình.')
    } else if (error.error === 'invalid_grant') {
      alert('Phiên đăng nhập đã hết hạn. Vui lòng thử lại.')
    } else {
      alert('Đăng nhập thất bại. Vui lòng thử lại.')
    }
  } finally {
    loading.value = false
  }
}

const handleDemoLogin = () => {
  // Demo login với dữ liệu giả
  const demoUser = {
    sub: 'demo-user-id',
    name: 'Demo User',
    email: 'demo@tiezshop.com',
    preferred_username: 'demo',
    roles: ['customer']
  }
  
  authStore.setUser(demoUser)
  authStore.setToken('demo-token')
  
  // Redirect về trang chủ
  router.push('/')
}
</script>

<style scoped>
.login {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem;
}

.login-container {
  background: white;
  border-radius: 15px;
  box-shadow: 0 20px 40px rgba(0,0,0,0.1);
  overflow: hidden;
  max-width: 500px;
  width: 100%;
}

.login-form {
  padding: 3rem;
  text-align: center;
}

.login-form h1 {
  color: #2c3e50;
  margin-bottom: 0.5rem;
  font-size: 2rem;
}

.login-form p {
  color: #7f8c8d;
  margin-bottom: 2rem;
}

.loading {
  padding: 2rem;
}

.loading p {
  color: #3498db;
  font-size: 1.1rem;
}

.login-options {
  margin-bottom: 2rem;
}

.keycloak-login-btn {
  background: #3498db;
  color: white;
  border: none;
  padding: 1rem 2rem;
  border-radius: 10px;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  width: 100%;
  margin-bottom: 1.5rem;
  transition: background 0.3s, transform 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.keycloak-login-btn:hover {
  background: #2980b9;
  transform: translateY(-2px);
}

.login-icon {
  font-size: 1.2rem;
}

.divider {
  position: relative;
  margin: 1.5rem 0;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: #ecf0f1;
}

.divider span {
  background: white;
  padding: 0 1rem;
  color: #7f8c8d;
  font-size: 0.9rem;
}

.demo-login {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 10px;
  margin-bottom: 1rem;
}

.demo-login p {
  margin-bottom: 1rem;
  color: #2c3e50;
  font-weight: 500;
}

.demo-login-btn {
  background: #27ae60;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.3s;
}

.demo-login-btn:hover {
  background: #229954;
}

.login-info {
  background: #e8f4fd;
  padding: 1.5rem;
  border-radius: 10px;
  text-align: left;
  border-left: 4px solid #3498db;
}

.login-info h3 {
  color: #2c3e50;
  margin-bottom: 1rem;
  font-size: 1rem;
}

.login-info ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.login-info li {
  margin-bottom: 0.5rem;
  color: #2c3e50;
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .login {
    padding: 1rem;
  }
  
  .login-form {
    padding: 2rem;
  }
  
  .login-form h1 {
    font-size: 1.5rem;
  }
}
</style>
