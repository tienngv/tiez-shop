// Keycloak Configuration Checker
// Chạy script này trong browser console để kiểm tra cấu hình

console.log('=== Keycloak Configuration Check ===');

// Kiểm tra URL hiện tại
console.log('Current URL:', window.location.href);
console.log('Origin:', window.location.origin);

// Kiểm tra Keycloak instance
if (window.keycloak) {
  console.log('Keycloak instance found');
  console.log('Keycloak config:', window.keycloak);
  console.log('Authenticated:', window.keycloak.authenticated);
} else {
  console.log('Keycloak instance not found');
}

// Kiểm tra các URL cần thiết
const requiredUrls = [
  window.location.origin,
  window.location.origin + '/',
  window.location.origin + '/silent-check-sso.html'
];

console.log('Required redirect URIs for Keycloak client:');
requiredUrls.forEach(url => console.log('- ' + url));

// Test Keycloak connection
async function testKeycloakConnection() {
  try {
    const response = await fetch('http://localhost:8180/realms/tiez-shop/.well-known/openid_configuration');
    const config = await response.json();
    console.log('Keycloak realm config:', config);
    return true;
  } catch (error) {
    console.error('Keycloak connection failed:', error);
    return false;
  }
}

// Chạy test
testKeycloakConnection().then(success => {
  if (success) {
    console.log('✅ Keycloak server is accessible');
  } else {
    console.log('❌ Keycloak server is not accessible');
    console.log('Please check:');
    console.log('1. Keycloak server is running on http://localhost:8180');
    console.log('2. Realm "tiez-shop" exists');
    console.log('3. Client "tienngv" exists');
  }
});
