// API Test Script
// Chạy script này trong browser console để test API

console.log('=== API Test Script ===');

// Test function
async function testUserAPI() {
  const userId = 'a38e6c14-504b-4935-a0b7-88560f2a433f'; // User ID từ curl command
  const token = localStorage.getItem('access_token');
  
  console.log('Testing API with:');
  console.log('- User ID:', userId);
  console.log('- Has Token:', !!token);
  console.log('- Token Preview:', token ? token.substring(0, 30) + '...' : 'No token');
  
  if (!token) {
    console.error('❌ No access token found in localStorage');
    return;
  }
  
  try {
    // Test 1: Direct fetch to API
    console.log('\n--- Test 1: Direct Fetch ---');
    const response = await fetch(`http://localhost:8080/user/${userId}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    console.log('Response Status:', response.status);
    console.log('Response Headers:', Object.fromEntries(response.headers.entries()));
    
    if (response.ok) {
      const data = await response.json();
      console.log('✅ Success:', data);
    } else {
      const errorText = await response.text();
      console.log('❌ Error:', errorText);
    }
    
  } catch (error) {
    console.error('❌ Network Error:', error);
  }
  
  try {
    // Test 2: Using axios
    console.log('\n--- Test 2: Using Axios ---');
    const axios = window.axios || (await import('axios')).default;
    
    const axiosResponse = await axios.get(`http://localhost:8080/user/${userId}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    console.log('✅ Axios Success:', axiosResponse.data);
    
  } catch (error) {
    console.error('❌ Axios Error:', {
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      message: error.message
    });
  }
}

// Test token validity
function testTokenValidity() {
  const token = localStorage.getItem('access_token');
  
  if (!token) {
    console.log('❌ No token found');
    return;
  }
  
  try {
    // Decode JWT token
    const parts = token.split('.');
    if (parts.length !== 3) {
      console.log('❌ Invalid token format');
      return;
    }
    
    const payload = JSON.parse(atob(parts[1]));
    console.log('Token Payload:', payload);
    
    const now = Math.floor(Date.now() / 1000);
    const exp = payload.exp;
    
    console.log('Current Time:', now);
    console.log('Token Expiry:', exp);
    console.log('Token Valid:', exp > now);
    console.log('Time Until Expiry:', exp - now, 'seconds');
    
  } catch (error) {
    console.error('❌ Error decoding token:', error);
  }
}

// Check Keycloak configuration
function checkKeycloakConfig() {
  console.log('=== Keycloak Configuration Check ===');
  
  // Check if Keycloak instance exists
  if (window.keycloak) {
    console.log('✅ Keycloak instance found');
    console.log('Keycloak Config:', {
      url: window.keycloak.authServerUrl,
      realm: window.keycloak.realm,
      clientId: window.keycloak.clientId,
      authenticated: window.keycloak.authenticated,
      token: window.keycloak.token ? window.keycloak.token.substring(0, 30) + '...' : 'No token'
    });
  } else {
    console.log('❌ Keycloak instance not found');
  }
  
  // Check localStorage
  console.log('LocalStorage Keys:', Object.keys(localStorage));
  console.log('Access Token in localStorage:', !!localStorage.getItem('access_token'));
}

// Run all tests
async function runAllTests() {
  console.log('🚀 Running all API tests...\n');
  
  checkKeycloakConfig();
  console.log('\n');
  
  testTokenValidity();
  console.log('\n');
  
  await testUserAPI();
}

// Export functions for manual testing
window.testUserAPI = testUserAPI;
window.testTokenValidity = testTokenValidity;
window.checkKeycloakConfig = checkKeycloakConfig;
window.runAllTests = runAllTests;

console.log('API Test functions available:');
console.log('- testUserAPI()');
console.log('- testTokenValidity()');
console.log('- checkKeycloakConfig()');
console.log('- runAllTests()');
console.log('\nRun runAllTests() to test everything');
