<template>
  <div class="profile">
    <div class="profile-header">
      <h1>Thông tin cá nhân</h1>
    </div>

    <div class="profile-content">
      <div class="profile-sidebar">
        <div class="profile-menu">
          <router-link to="/profile" class="menu-item" exact-active-class="active">
            <i class="menu-icon">👤</i>
            Thông tin cá nhân
          </router-link>
          <router-link to="/orders" class="menu-item" exact-active-class="active">
            <i class="menu-icon">📦</i>
            Đơn hàng của tôi
          </router-link>
          <router-link to="/profile/settings" class="menu-item" exact-active-class="active">
            <i class="menu-icon">⚙️</i>
            Cài đặt
          </router-link>
        </div>
      </div>

      <div class="profile-main">
        <!-- Loading State -->
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>Đang tải thông tin...</p>
        </div>

        <!-- Error Message -->
        <div v-if="error" class="error-message">
          <div class="error-icon">⚠️</div>
          <p>{{ error }}</p>
          <button @click="loadUserProfile" class="retry-btn">Thử lại</button>
        </div>

        <!-- Success Message -->
        <div v-if="success" class="success-message">
          <div class="success-icon">✅</div>
          <p>{{ success }}</p>
        </div>

        <!-- Profile Content -->
        <div v-if="!loading && !error" class="profile-card">
          <div class="profile-avatar">
            <div class="avatar">
              <img v-if="profile.avatar" :src="profile.avatar" :alt="profile.name" class="avatar-image">
              <span v-else>{{ userInitials }}</span>
            </div>
            <button class="change-avatar-btn">Thay đổi ảnh</button>
          </div>

          <div class="profile-form">
            <h2>Thông tin cá nhân</h2>
            
            <div class="form-group">
              <label>ID User</label>
              <input v-model="profile.id" type="text" class="form-input" disabled>
            </div>
            
            <div class="form-group">
              <label>Họ và tên</label>
              <input v-model="profile.name" type="text" class="form-input">
            </div>

            <div class="form-group">
              <label>Email</label>
              <input v-model="profile.email" type="email" class="form-input" disabled>
            </div>

            <div class="form-group">
              <label>Số điện thoại</label>
              <input v-model="profile.phone" type="tel" class="form-input">
            </div>

            <div class="form-group">
              <label>Ngày sinh</label>
              <input v-model="profile.birthday" type="date" class="form-input">
            </div>

            <div class="form-group">
              <label>Giới tính</label>
              <select v-model="profile.gender" class="form-select">
                <option value="">Chọn giới tính</option>
                <option value="male">Nam</option>
                <option value="female">Nữ</option>
                <option value="other">Khác</option>
              </select>
            </div>

            <div class="form-group">
              <label>Địa chỉ</label>
              <textarea v-model="profile.address" class="form-textarea" rows="3"></textarea>
            </div>

            <div class="form-group">
              <label>Ngày tạo tài khoản</label>
              <input :value="formatDate(profile.createdAt)" type="text" class="form-input" disabled>
            </div>

            <div class="form-actions">
              <button @click="saveProfile" class="save-btn" :disabled="loading">
                {{ loading ? 'Đang lưu...' : 'Lưu thông tin' }}
              </button>
              <button @click="resetProfile" class="reset-btn" :disabled="loading">
                Đặt lại
              </button>
            </div>
          </div>
        </div>

        <div class="profile-stats">
          <div class="stat-card">
            <div class="stat-icon">📦</div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.totalOrders }}</div>
              <div class="stat-label">Tổng đơn hàng</div>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon">💰</div>
            <div class="stat-info">
              <div class="stat-number">{{ formatPrice(stats.totalSpent) }}</div>
              <div class="stat-label">Tổng chi tiêu</div>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon">⭐</div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.memberSince }}</div>
              <div class="stat-label">Thành viên từ</div>
            </div>
          </div>
        </div>

        <!-- Debug Panel -->
        <DebugPanel />
        
        <!-- Cleanup Guide -->
        <CleanupGuide />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth.js'
import { userApi } from '../services/api.js'
import DebugPanel from '../components/DebugPanel.vue'
import CleanupGuide from '../components/CleanupGuide.vue'

const authStore = useAuthStore()

const profile = ref({
  id: '',
  name: '',
  email: '',
  phone: '',
  birthday: '',
  gender: '',
  address: '',
  avatar: '',
  createdAt: ''
})

const stats = ref({
  totalOrders: 0,
  totalSpent: 0,
  memberSince: ''
})

const loading = ref(false)
const error = ref('')
const success = ref('')

const userInitials = computed(() => {
  if (profile.value.name) {
    return profile.value.name.split(' ').map(n => n[0]).join('').toUpperCase()
  }
  return 'U'
})

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price)
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('vi-VN')
}

const loadUserProfile = async () => {
  if (!authStore.userId) {
    error.value = 'Không tìm thấy thông tin user'
    return
  }

  loading.value = true
  error.value = ''
  
  try {
    // Gọi API để lấy thông tin user
    const userData = await userApi.getUserById(authStore.userId)
    
    // Cập nhật profile với dữ liệu từ API
    profile.value = {
      id: userData.id || authStore.userId,
      name: userData.name || userData.fullName || '',
      email: userData.email || '',
      phone: userData.phone || userData.phoneNumber || '',
      birthday: userData.birthday || userData.dateOfBirth || '',
      gender: userData.gender || '',
      address: userData.address || '',
      avatar: userData.avatar || userData.profilePicture || '',
      createdAt: userData.createdAt || userData.created_at || ''
    }

    // Cập nhật stats
    stats.value = {
      totalOrders: userData.totalOrders || userData.order_count || 0,
      totalSpent: userData.totalSpent || userData.total_spent || 0,
      memberSince: userData.createdAt ? new Date(userData.createdAt).getFullYear().toString() : '2023'
    }

  } catch (err) {
    console.error('Error loading user profile:', err)
    error.value = 'Không thể tải thông tin user. Vui lòng thử lại.'
    
    // Fallback: sử dụng dữ liệu từ Keycloak
    if (authStore.userInfo) {
      profile.value.name = authStore.userInfo.name || ''
      profile.value.email = authStore.userInfo.email || ''
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadUserProfile()
})

const saveProfile = async () => {
  if (!authStore.userId) {
    error.value = 'Không tìm thấy thông tin user'
    return
  }

  loading.value = true
  error.value = ''
  success.value = ''

  try {
    const updateData = {
      name: profile.value.name,
      phone: profile.value.phone,
      birthday: profile.value.birthday,
      gender: profile.value.gender,
      address: profile.value.address
    }

    await userApi.updateUser(authStore.userId, updateData)
    success.value = 'Thông tin đã được cập nhật thành công!'
    
    // Reload profile để lấy dữ liệu mới nhất
    setTimeout(() => {
      loadUserProfile()
    }, 1000)

  } catch (err) {
    console.error('Error updating user profile:', err)
    error.value = 'Không thể cập nhật thông tin. Vui lòng thử lại.'
  } finally {
    loading.value = false
  }
}

const resetProfile = () => {
  loadUserProfile()
}
</script>

<style scoped>
.profile {
  padding: 2rem 20px;
  width: 100%;
  margin: 0 auto;
}

.profile-header {
  margin-bottom: 2rem;
}

.profile-header h1 {
  color: #2c3e50;
  text-align: center;
}

.profile-content {
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: 2rem;
  max-width: 1400px;
  margin: 0 auto;
}

.profile-sidebar {
  position: sticky;
  top: 2rem;
}

.profile-menu {
  background: white;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem 1.5rem;
  color: #2c3e50;
  text-decoration: none;
  transition: background 0.3s;
  border-bottom: 1px solid #ecf0f1;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:hover,
.menu-item.active {
  background: #3498db;
  color: white;
}

.menu-icon {
  font-size: 1.2rem;
}

.profile-main {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

/* Loading State */
.loading-state {
  text-align: center;
  padding: 3rem;
}

.loading-spinner {
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

/* Error Message */
.error-message {
  background: #ffe6e6;
  border: 1px solid #ff9999;
  border-radius: 8px;
  padding: 1.5rem;
  text-align: center;
  color: #d63031;
}

.error-icon {
  font-size: 2rem;
  margin-bottom: 1rem;
}

.retry-btn {
  background: #e74c3c;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 1rem;
}

.retry-btn:hover {
  background: #c0392b;
}

/* Success Message */
.success-message {
  background: #e6ffe6;
  border: 1px solid #99ff99;
  border-radius: 8px;
  padding: 1.5rem;
  text-align: center;
  color: #00b894;
}

.success-icon {
  font-size: 2rem;
  margin-bottom: 1rem;
}

.profile-card {
  background: white;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  padding: 2rem;
}

.profile-avatar {
  text-align: center;
  margin-bottom: 2rem;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: #3498db;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  font-weight: bold;
  margin: 0 auto 1rem;
  overflow: hidden;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.change-avatar-btn {
  background: transparent;
  color: #3498db;
  border: 2px solid #3498db;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.change-avatar-btn:hover {
  background: #3498db;
  color: white;
}

.profile-form h2 {
  color: #2c3e50;
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #2c3e50;
  font-weight: 500;
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #ecf0f1;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: #3498db;
}

.form-input:disabled {
  background: #f8f9fa;
  color: #7f8c8d;
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

.save-btn,
.reset-btn {
  padding: 0.75rem 2rem;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
}

.save-btn {
  background: #27ae60;
  color: white;
}

.save-btn:hover {
  background: #229954;
}

.reset-btn {
  background: #95a5a6;
  color: white;
}

.reset-btn:hover {
  background: #7f8c8d;
}

.profile-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  max-width: 1400px;
  margin: 0 auto;
}

.stat-card {
  background: white;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
}

.stat-icon {
  font-size: 2rem;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 1.5rem;
  font-weight: bold;
  color: #2c3e50;
}

.stat-label {
  color: #7f8c8d;
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .profile-content {
    grid-template-columns: 1fr;
  }
  
  .profile-sidebar {
    position: static;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .profile-stats {
    grid-template-columns: 1fr;
  }
}
</style>
