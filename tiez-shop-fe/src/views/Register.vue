<template>
  <div class="register">
    <div class="register-container">
      <div class="register-form">
        <h1>Đăng ký tài khoản</h1>
        <p>Tạo tài khoản mới để truy cập vào TiezShop</p>
        
        <form @submit.prevent="handleRegister" class="form">
          <div class="form-group">
            <label for="email">Email *</label>
            <input 
              type="email" 
              id="email" 
              v-model="formData.email" 
              required 
              placeholder="Nhập email của bạn"
            />
          </div>
          
          <div class="form-group">
            <label for="username">Tên đăng nhập *</label>
            <input 
              type="text" 
              id="username" 
              v-model="formData.username" 
              required 
              placeholder="Nhập tên đăng nhập"
            />
          </div>
          
          <div class="form-group">
            <label for="password">Mật khẩu *</label>
            <input 
              type="password" 
              id="password" 
              v-model="formData.password" 
              required 
              placeholder="Nhập mật khẩu"
            />
          </div>
          
          <div class="form-group">
            <label for="confirmPassword">Xác nhận mật khẩu *</label>
            <input 
              type="password" 
              id="confirmPassword" 
              v-model="formData.confirmPassword" 
              required 
              placeholder="Nhập lại mật khẩu"
            />
          </div>
          
          <div class="form-group">
            <label for="phoneNumber">Số điện thoại *</label>
            <input 
              type="tel" 
              id="phoneNumber" 
              v-model="formData.phoneNumber" 
              required 
              placeholder="Nhập số điện thoại"
            />
          </div>
          
          <div class="form-group">
            <label for="fullName">Họ và tên *</label>
            <input 
              type="text" 
              id="fullName" 
              v-model="formData.fullName" 
              required 
              placeholder="Nhập họ và tên đầy đủ"
            />
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label for="firstName">Tên *</label>
              <input 
                type="text" 
                id="firstName" 
                v-model="formData.firstName" 
                required 
                placeholder="Tên"
              />
            </div>
            
            <div class="form-group">
              <label for="lastName">Họ *</label>
              <input 
                type="text" 
                id="lastName" 
                v-model="formData.lastName" 
                required 
                placeholder="Họ"
              />
            </div>
          </div>
          
          <div class="form-group">
            <label for="dob">Ngày sinh</label>
            <input 
              type="date" 
              id="dob" 
              v-model="formData.dob" 
              placeholder="Chọn ngày sinh"
            />
          </div>
          
          <div v-if="errorMessage" class="error-message">
            {{ errorMessage }}
          </div>
          
          <div v-if="successMessage" class="success-message">
            {{ successMessage }}
          </div>
          
          <button type="submit" :disabled="loading" class="register-btn">
            <div v-if="loading" class="spinner"></div>
            {{ loading ? 'Đang đăng ký...' : 'Đăng ký' }}
          </button>
        </form>
        
        <div class="login-link">
          <p>Đã có tài khoản? <a href="#" @click="handleLogin">Đăng nhập ngay</a></p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '../services/api.js'

const router = useRouter()
const loading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

const formData = ref({
  email: '',
  username: '',
  password: '',
  confirmPassword: '',
  phoneNumber: '',
  fullName: '',
  firstName: '',
  lastName: '',
  dob: ''
})

const handleRegister = async () => {
  // Reset messages
  errorMessage.value = ''
  successMessage.value = ''
  
  // Validate form
  if (formData.value.password !== formData.value.confirmPassword) {
    errorMessage.value = 'Mật khẩu xác nhận không khớp!'
    return
  }
  
  if (formData.value.password.length < 6) {
    errorMessage.value = 'Mật khẩu phải có ít nhất 6 ký tự!'
    return
  }
  
  loading.value = true
  
  try {
    const registerData = {
      email: formData.value.email,
      username: formData.value.username,
      password: formData.value.password,
      phoneNumber: formData.value.phoneNumber,
      fullName: formData.value.fullName,
      firstName: formData.value.firstName,
      lastName: formData.value.lastName,
      dob: formData.value.dob || null
    }
    
    console.log('Registering user:', registerData)
    const response = await userApi.register(registerData)
    console.log('Register response:', response)
    
    if (response.errorCode === '2000' || response.result) {
      successMessage.value = 'Đăng ký thành công! Bây giờ bạn có thể đăng nhập.'
      
      // Clear form
      formData.value = {
        email: '',
        username: '',
        password: '',
        confirmPassword: '',
        phoneNumber: '',
        fullName: '',
        firstName: '',
        lastName: '',
        dob: ''
      }
    } else {
      errorMessage.value = response.message || 'Có lỗi xảy ra khi đăng ký'
    }
  } catch (error) {
    console.error('Register error:', error)
    errorMessage.value = error.response?.data?.message || error.message || 'Có lỗi xảy ra khi đăng ký'
  } finally {
    loading.value = false
  }
}

const handleLogin = () => {
  // Redirect directly to Keycloak
  const keycloakUrl = `http://localhost:8180/realms/tiez-shop/protocol/openid-connect/auth?` +
    `client_id=tienngv&` +
    `redirect_uri=${encodeURIComponent('http://localhost:5173/callback')}&` +
    `response_type=code&` +
    `scope=openid`
  
  console.log('Redirecting to Keycloak:', keycloakUrl)
  window.location.href = keycloakUrl
}
</script>

<style scoped>
.register {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem;
}

.register-container {
  background: white;
  border-radius: 15px;
  box-shadow: 0 20px 40px rgba(0,0,0,0.1);
  overflow: hidden;
  max-width: 600px;
  width: 100%;
}

.register-form {
  padding: 3rem;
}

.register-form h1 {
  color: #2c3e50;
  margin-bottom: 0.5rem;
  font-size: 2rem;
  text-align: center;
}

.register-form p {
  color: #7f8c8d;
  margin-bottom: 2rem;
  text-align: center;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  color: #2c3e50;
  font-weight: 500;
  margin-bottom: 0.5rem;
}

.form-group input {
  padding: 0.75rem;
  border: 2px solid #ecf0f1;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #3498db;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.register-btn {
  background: #27ae60;
  color: white;
  border: none;
  padding: 1rem 2rem;
  border-radius: 10px;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.3s, transform 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.register-btn:hover:not(:disabled) {
  background: #229954;
  transform: translateY(-2px);
}

.register-btn:disabled {
  background: #95a5a6;
  cursor: not-allowed;
  transform: none;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #ffffff;
  border-top: 2px solid transparent;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  background: #e74c3c;
  color: white;
  padding: 1rem;
  border-radius: 8px;
  text-align: center;
  font-weight: 500;
}

.success-message {
  background: #27ae60;
  color: white;
  padding: 1rem;
  border-radius: 8px;
  text-align: center;
  font-weight: 500;
}

.login-link {
  text-align: center;
  margin-top: 2rem;
}

.login-link p {
  color: #7f8c8d;
  margin: 0;
}

.login-link a {
  color: #3498db;
  text-decoration: none;
  font-weight: 500;
}

.login-link a:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .register {
    padding: 1rem;
  }
  
  .register-form {
    padding: 2rem;
  }
  
  .register-form h1 {
    font-size: 1.5rem;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
