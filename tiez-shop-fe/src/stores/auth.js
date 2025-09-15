import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { initKeycloak, isAuthenticated, getUserInfo, getToken, resetKeycloak } from '../services/keycloak.js'
import { tokenUtils } from '../utils/tokenUtils.js'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const token = ref(null)
  const isLoggedIn = ref(false)
  const loading = ref(false)

  // Computed properties
  const userInfo = computed(() => user.value)
  const isAuthenticated = computed(() => isLoggedIn.value)
  const userId = computed(() => user.value?.sub || user.value?.id)

  // Actions
  const initializeAuth = async () => {
    loading.value = true
    try {
      // Check if we have a valid token in localStorage first
      const existingToken = localStorage.getItem('access_token')
      if (existingToken) {
        const tokenInfo = tokenUtils.getTokenInfo(existingToken)
        console.log('Existing token info:', tokenInfo)
        
        if (!tokenInfo.valid) {
          console.log('Existing token is invalid, clearing...', tokenInfo.error)
          clearOldTokens()
        }
      }
      
      const authenticated = await initKeycloak()
      if (authenticated) {
        user.value = getUserInfo()
        token.value = getToken()
        isLoggedIn.value = true
        
        // Lưu token vào localStorage để sử dụng cho API calls
        if (token.value) {
          localStorage.setItem('access_token', token.value)
          console.log('Token saved to localStorage:', token.value.substring(0, 20) + '...')
        }
      } else {
        // Clear any existing tokens if not authenticated
        clearOldTokens()
        console.log('Not authenticated, user needs to login')
      }
    } catch (error) {
      console.error('Keycloak initialization failed:', error)
      clearOldTokens()
      // Don't throw error, just log it and continue
    } finally {
      loading.value = false
    }
  }

  const clearOldTokens = () => {
    resetKeycloak()
    console.log('All tokens and state cleared')
  }

  const setUser = (userData) => {
    user.value = userData
    isLoggedIn.value = true
  }

  const setToken = (tokenData) => {
    token.value = tokenData
    if (tokenData) {
      localStorage.setItem('access_token', tokenData)
    }
  }

  const logout = () => {
    user.value = null
    token.value = null
    isLoggedIn.value = false
    clearOldTokens()
    console.log('User logged out, tokens cleared')
  }

  return {
    user,
    token,
    isLoggedIn,
    loading,
    userInfo,
    isAuthenticated,
    userId,
    initializeAuth,
    setUser,
    setToken,
    logout
  }
})
