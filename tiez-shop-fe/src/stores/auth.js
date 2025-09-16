import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

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
      // Check if we have a valid token in localStorage
      const existingToken = localStorage.getItem('access_token')
      if (existingToken) {
        // Simple token validation - check if it's not expired
        try {
          const tokenParts = existingToken.split('.')
          if (tokenParts.length === 3) {
            const payload = JSON.parse(atob(tokenParts[1]))
            const now = Math.floor(Date.now() / 1000)
            
            if (payload.exp && payload.exp > now) {
              // Token is still valid
              token.value = existingToken
              isLoggedIn.value = true
              
              // Try to get user info from token
              if (payload.sub) {
                user.value = {
                  sub: payload.sub,
                  name: payload.name || payload.preferred_username,
                  email: payload.email,
                  preferred_username: payload.preferred_username,
                  roles: payload.realm_access?.roles || ['customer']
                }
              }
              
              console.log('User authenticated with existing token')
              return
            }
          }
        } catch (e) {
          console.log('Token validation failed:', e)
        }
      }
      
      // No valid token found
      clearOldTokens()
      console.log('No valid token found, user needs to login')
    } catch (error) {
      console.error('Auth initialization failed:', error)
      clearOldTokens()
    } finally {
      loading.value = false
    }
  }

  const clearOldTokens = () => {
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
    localStorage.removeItem('keycloak_token')
    user.value = null
    token.value = null
    isLoggedIn.value = false
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
