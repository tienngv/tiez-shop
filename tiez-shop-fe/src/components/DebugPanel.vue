<template>
  <div class="debug-panel">
    <h3>🔍 Debug Authentication</h3>
    
    <div class="debug-section">
      <h4>Token Information</h4>
      <div class="debug-item">
        <label>Has Token:</label>
        <span :class="hasToken ? 'success' : 'error'">
          {{ hasToken ? '✅ Yes' : '❌ No' }}
        </span>
      </div>
      <div class="debug-item">
        <label>Token Preview:</label>
        <span class="token-preview">{{ tokenPreview }}</span>
      </div>
      <div class="debug-item">
        <label>Token Expiry:</label>
        <span>{{ tokenExpiry }}</span>
      </div>
    </div>

    <div class="debug-section">
      <h4>User Information</h4>
      <div class="debug-item">
        <label>User ID:</label>
        <span>{{ authStore.userId || 'Not available' }}</span>
      </div>
      <div class="debug-item">
        <label>Is Authenticated:</label>
        <span :class="authStore.isAuthenticated ? 'success' : 'error'">
          {{ authStore.isAuthenticated ? '✅ Yes' : '❌ No' }}
        </span>
      </div>
    </div>

    <div class="debug-section">
      <h4>API Test</h4>
      <button @click="testApi" :disabled="loading" class="test-btn">
        {{ loading ? 'Testing...' : 'Test API Call' }}
      </button>
      <div v-if="apiResult" class="api-result">
        <h5>API Result:</h5>
        <pre>{{ JSON.stringify(apiResult, null, 2) }}</pre>
      </div>
      <div v-if="apiError" class="api-error">
        <h5>API Error:</h5>
        <pre>{{ JSON.stringify(apiError, null, 2) }}</pre>
      </div>
    </div>

    <div class="debug-section">
      <h4>Actions</h4>
      <button @click="refreshToken" class="action-btn">Refresh Token</button>
      <button @click="clearToken" class="action-btn danger">Clear Token</button>
      <button @click="forceCleanup" class="action-btn danger">Force Cleanup</button>
      <button @click="logout" class="action-btn danger">Logout</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth.js'
import { userApi } from '../services/api.js'
import { refreshToken as refreshKeycloakToken, logout as keycloakLogout } from '../services/keycloak.js'
import { tokenUtils } from '../utils/tokenUtils.js'

const authStore = useAuthStore()
const loading = ref(false)
const apiResult = ref(null)
const apiError = ref(null)

const hasToken = computed(() => {
  return !!localStorage.getItem('access_token')
})

const tokenPreview = computed(() => {
  const token = localStorage.getItem('access_token')
  return token ? token.substring(0, 30) + '...' : 'No token'
})

const tokenExpiry = computed(() => {
  const token = localStorage.getItem('access_token')
  if (!token) return 'No token'
  
  const tokenInfo = tokenUtils.getTokenInfo(token)
  if (!tokenInfo.valid) {
    return `${tokenInfo.error} (EXPIRED)`
  }
  
  return `${tokenInfo.expiry.toLocaleString()} (Valid - ${Math.floor(tokenInfo.timeUntilExpiry / 60)} min left)`
})

const testApi = async () => {
  if (!authStore.userId) {
    apiError.value = { error: 'No user ID available' }
    return
  }

  loading.value = true
  apiResult.value = null
  apiError.value = null

  try {
    const result = await userApi.getUserById(authStore.userId)
    apiResult.value = result
  } catch (error) {
    apiError.value = {
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      message: error.message
    }
  } finally {
    loading.value = false
  }
}

const refreshToken = async () => {
  try {
    await refreshKeycloakToken()
    console.log('Token refreshed successfully')
  } catch (error) {
    console.error('Token refresh failed:', error)
  }
}

const clearToken = () => {
  tokenUtils.clearAllTokens()
  console.log('All tokens cleared')
}

const forceCleanup = () => {
  if (confirm('This will clear ALL authentication data and reload the page. Continue?')) {
    // Import and run ultra cleanup script
    import('../utils/ultra-cleanup.js').then(() => {
      console.log('Ultra cleanup script loaded and executed');
    }).catch(err => {
      console.error('Failed to load cleanup script:', err);
      // Fallback cleanup
      localStorage.clear();
      sessionStorage.clear();
      window.location.reload();
    });
  }
}

const logout = () => {
  keycloakLogout()
}
</script>

<style scoped>
.debug-panel {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  padding: 1.5rem;
  margin: 2rem 0;
  font-family: monospace;
}

.debug-panel h3 {
  margin: 0 0 1.5rem 0;
  color: #2c3e50;
}

.debug-section {
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #dee2e6;
}

.debug-section:last-child {
  border-bottom: none;
}

.debug-section h4 {
  margin: 0 0 1rem 0;
  color: #495057;
  font-size: 1rem;
}

.debug-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
  padding: 0.25rem 0;
}

.debug-item label {
  font-weight: bold;
  color: #6c757d;
}

.success {
  color: #28a745;
}

.error {
  color: #dc3545;
}

.token-preview {
  font-family: monospace;
  background: #e9ecef;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
}

.test-btn, .action-btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 0.5rem;
  margin-bottom: 0.5rem;
}

.test-btn:hover, .action-btn:hover {
  background: #0056b3;
}

.action-btn.danger {
  background: #dc3545;
}

.action-btn.danger:hover {
  background: #c82333;
}

.test-btn:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

.api-result, .api-error {
  margin-top: 1rem;
  padding: 1rem;
  border-radius: 4px;
  background: white;
  border: 1px solid #dee2e6;
}

.api-result {
  border-left: 4px solid #28a745;
}

.api-error {
  border-left: 4px solid #dc3545;
}

.api-result h5, .api-error h5 {
  margin: 0 0 0.5rem 0;
  color: #495057;
}

pre {
  margin: 0;
  font-size: 0.8rem;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
