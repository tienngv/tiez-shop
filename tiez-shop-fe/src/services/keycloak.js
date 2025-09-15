// Enhanced Keycloak initialization with better error handling
import Keycloak from 'keycloak-js'

// Cấu hình Keycloak
const keycloakConfig = {
  url: 'http://localhost:8180', // URL của Keycloak server
  realm: 'tiez-shop', // Tên realm
  clientId: 'tienngv', // Client ID
  // Additional config to prevent session conflicts
  checkLoginIframe: false,
  checkLoginIframeInterval: 0,
  enableLogging: true,
  flow: 'standard',
  responseMode: 'fragment',
  pkceMethod: 'S256'
}

// Khởi tạo Keycloak instance
const keycloak = new Keycloak(keycloakConfig)

// Flag để track initialization state
let isInitializing = false
let initializationPromise = null

// Hàm khởi tạo Keycloak với retry mechanism
export const initKeycloak = () => {
  // Prevent multiple simultaneous initializations
  if (isInitializing && initializationPromise) {
    return initializationPromise
  }
  
  isInitializing = true
  
  initializationPromise = new Promise(async (resolve, reject) => {
    try {
      // Clear any existing tokens first
      localStorage.removeItem('access_token')
      localStorage.removeItem('refresh_token')
      localStorage.removeItem('keycloak_token')
      
      console.log('Starting Keycloak initialization...')
      
      const authenticated = await keycloak.init({
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
        pkceMethod: 'S256',
        redirectUri: window.location.origin,
        checkLoginIframe: false,
        checkLoginIframeInterval: 0,
        enableLogging: true,
        flow: 'standard',
        responseMode: 'fragment',
        // Additional options to prevent conflicts
        promiseType: 'native',
        adapter: 'default'
      })
      
      console.log('Keycloak initialization completed:', authenticated)
      resolve(authenticated)
      
    } catch (error) {
      console.error('Keycloak initialization failed:', error)
      
      // Clear tokens on error
      localStorage.removeItem('access_token')
      localStorage.removeItem('refresh_token')
      localStorage.removeItem('keycloak_token')
      
      // Don't reject, just resolve with false
      resolve(false)
    } finally {
      isInitializing = false
      initializationPromise = null
    }
  })
  
  return initializationPromise
}

// Hàm đăng nhập với cleanup
export const login = () => {
  // Clear old tokens before login
  localStorage.removeItem('access_token')
  localStorage.removeItem('refresh_token')
  localStorage.removeItem('keycloak_token')
  console.log('Clearing old tokens before login')
  
  // Reset initialization state
  isInitializing = false
  initializationPromise = null
  
  return keycloak.login()
}

// Hàm đăng xuất với cleanup
export const logout = () => {
  // Clear tokens before logout
  localStorage.removeItem('access_token')
  localStorage.removeItem('refresh_token')
  localStorage.removeItem('keycloak_token')
  console.log('Clearing tokens before logout')
  
  // Reset initialization state
  isInitializing = false
  initializationPromise = null
  
  return keycloak.logout()
}

// Hàm lấy token
export const getToken = () => {
  return keycloak.token
}

// Hàm kiểm tra trạng thái đăng nhập
export const isAuthenticated = () => {
  return keycloak.authenticated
}

// Hàm lấy thông tin user
export const getUserInfo = () => {
  return keycloak.tokenParsed
}

// Hàm refresh token với better error handling
export const refreshToken = () => {
  return keycloak.updateToken(30).then((refreshed) => {
    if (refreshed) {
      console.log('Token refreshed successfully')
      // Update localStorage with new token
      localStorage.setItem('access_token', keycloak.token)
    } else {
      console.log('Token is still valid')
    }
    return refreshed
  }).catch((error) => {
    console.error('Token refresh failed:', error)
    // Clear tokens on refresh failure
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
    localStorage.removeItem('keycloak_token')
    throw error
  })
}

// Hàm kiểm tra role
export const hasRole = (role) => {
  return keycloak.hasRealmRole(role)
}

// Hàm reset Keycloak state
export const resetKeycloak = () => {
  console.log('Resetting Keycloak state')
  isInitializing = false
  initializationPromise = null
  
  // Clear all tokens
  localStorage.removeItem('access_token')
  localStorage.removeItem('refresh_token')
  localStorage.removeItem('keycloak_token')
  
  // Clear Keycloak cookies if possible
  document.cookie.split(";").forEach(function(c) { 
    document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/"); 
  });
}

export default keycloak