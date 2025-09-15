// Token validation utilities
export const tokenUtils = {
  // Check if token exists and is valid
  isValidToken: (token) => {
    if (!token) return false
    
    try {
      const parts = token.split('.')
      if (parts.length !== 3) return false
      
      const payload = JSON.parse(atob(parts[1]))
      const now = Math.floor(Date.now() / 1000)
      
      // Check if token is expired
      if (payload.exp < now) return false
      
      // Check if token has required fields
      if (!payload.sub || !payload.iss) return false
      
      return true
    } catch (error) {
      console.error('Token validation error:', error)
      return false
    }
  },

  // Get token payload
  getTokenPayload: (token) => {
    if (!token) return null
    
    try {
      const parts = token.split('.')
      if (parts.length !== 3) return null
      
      return JSON.parse(atob(parts[1]))
    } catch (error) {
      console.error('Token parsing error:', error)
      return null
    }
  },

  // Get token expiry time
  getTokenExpiry: (token) => {
    const payload = tokenUtils.getTokenPayload(token)
    if (!payload || !payload.exp) return null
    
    return new Date(payload.exp * 1000)
  },

  // Check if token expires soon (within 5 minutes)
  isTokenExpiringSoon: (token, minutes = 5) => {
    const payload = tokenUtils.getTokenPayload(token)
    if (!payload || !payload.exp) return true
    
    const now = Math.floor(Date.now() / 1000)
    const expiryBuffer = minutes * 60
    
    return (payload.exp - now) < expiryBuffer
  },

  // Clear all tokens from localStorage
  clearAllTokens: () => {
    const keysToRemove = [
      'access_token',
      'refresh_token', 
      'keycloak_token',
      'id_token'
    ]
    
    keysToRemove.forEach(key => {
      localStorage.removeItem(key)
    })
    
    console.log('All tokens cleared from localStorage')
  },

  // Get token info for debugging
  getTokenInfo: (token) => {
    if (!token) return { valid: false, error: 'No token provided' }
    
    try {
      const payload = tokenUtils.getTokenPayload(token)
      if (!payload) return { valid: false, error: 'Invalid token format' }
      
      const now = Math.floor(Date.now() / 1000)
      const isExpired = payload.exp < now
      const timeUntilExpiry = payload.exp - now
      
      return {
        valid: !isExpired,
        expired: isExpired,
        payload,
        expiry: new Date(payload.exp * 1000),
        timeUntilExpiry,
        subject: payload.sub,
        issuer: payload.iss,
        audience: payload.aud
      }
    } catch (error) {
      return { valid: false, error: error.message }
    }
  }
}

// Auto-clear expired tokens on page load
export const autoCleanupTokens = () => {
  const token = localStorage.getItem('access_token')
  if (token && !tokenUtils.isValidToken(token)) {
    console.log('Auto-clearing expired token')
    tokenUtils.clearAllTokens()
  }
}

// Run auto cleanup when module loads
autoCleanupTokens()
