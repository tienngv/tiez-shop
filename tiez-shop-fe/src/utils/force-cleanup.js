// Force clear all Keycloak state and cookies
// Chạy script này trong browser console để clear hoàn toàn

console.log('🧹 Starting complete Keycloak cleanup...');

// 1. Clear localStorage
const keysToRemove = [
  'access_token',
  'refresh_token', 
  'keycloak_token',
  'id_token',
  'kc-callback',
  'kc-state'
];

keysToRemove.forEach(key => {
  if (localStorage.getItem(key)) {
    localStorage.removeItem(key);
    console.log(`✅ Removed ${key} from localStorage`);
  }
});

// 2. Clear sessionStorage
keysToRemove.forEach(key => {
  if (sessionStorage.getItem(key)) {
    sessionStorage.removeItem(key);
    console.log(`✅ Removed ${key} from sessionStorage`);
  }
});

// 3. Clear all cookies
function clearAllCookies() {
  document.cookie.split(";").forEach(function(c) { 
    const cookie = c.replace(/^ +/, "").split("=")[0];
    document.cookie = cookie + "=;expires=" + new Date().toUTCString() + ";path=/";
    document.cookie = cookie + "=;expires=" + new Date().toUTCString() + ";path=/;domain=localhost";
    document.cookie = cookie + "=;expires=" + new Date().toUTCString() + ";path=/;domain=.localhost";
    console.log(`✅ Cleared cookie: ${cookie}`);
  });
}

clearAllCookies();

// 4. Clear IndexedDB (if any)
if ('indexedDB' in window) {
  indexedDB.databases().then(databases => {
    databases.forEach(db => {
      if (db.name.includes('keycloak') || db.name.includes('auth')) {
        indexedDB.deleteDatabase(db.name);
        console.log(`✅ Deleted IndexedDB: ${db.name}`);
      }
    });
  }).catch(err => console.log('No IndexedDB to clear'));
}

// 5. Clear any global Keycloak instance
if (window.keycloak) {
  try {
    window.keycloak.logout();
    console.log('✅ Called Keycloak logout');
  } catch (e) {
    console.log('⚠️ Keycloak logout failed:', e);
  }
}

// 6. Clear any auth-related global variables
const globalKeys = ['keycloak', 'auth', 'token', 'user'];
globalKeys.forEach(key => {
  if (window[key]) {
    delete window[key];
    console.log(`✅ Cleared global variable: ${key}`);
  }
});

console.log('🎉 Complete cleanup finished!');
console.log('🔄 Please reload the page now');

// Auto reload after 2 seconds
setTimeout(() => {
  console.log('🔄 Reloading page...');
  window.location.reload();
}, 2000);
