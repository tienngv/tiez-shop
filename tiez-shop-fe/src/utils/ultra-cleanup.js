// Ultra-strong Keycloak cleanup script
// This will completely reset all authentication state

console.log('🔥 Starting ULTRA cleanup...');

// 1. Clear ALL localStorage
console.log('🧹 Clearing localStorage...');
localStorage.clear();

// 2. Clear ALL sessionStorage  
console.log('🧹 Clearing sessionStorage...');
sessionStorage.clear();

// 3. Clear ALL cookies aggressively
console.log('🧹 Clearing ALL cookies...');
function clearAllCookiesAggressively() {
  const cookies = document.cookie.split(";");
  cookies.forEach(cookie => {
    const eqPos = cookie.indexOf("=");
    const name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
    // Clear for current domain
    document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/";
    document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/;domain=localhost";
    document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/;domain=.localhost";
    document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/;domain=127.0.0.1";
    document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/;domain=.127.0.0.1";
    console.log(`✅ Cleared cookie: ${name}`);
  });
}
clearAllCookiesAggressively();

// 4. Clear IndexedDB completely
console.log('🧹 Clearing IndexedDB...');
if ('indexedDB' in window) {
  indexedDB.databases().then(databases => {
    databases.forEach(db => {
      indexedDB.deleteDatabase(db.name);
      console.log(`✅ Deleted IndexedDB: ${db.name}`);
    });
  }).catch(err => console.log('No IndexedDB to clear'));
}

// 5. Clear WebSQL (if exists)
console.log('🧹 Clearing WebSQL...');
if (window.openDatabase) {
  try {
    const db = window.openDatabase('', '', '', '');
    db.transaction(function(tx) {
      tx.executeSql('DELETE FROM keycloak');
    });
  } catch (e) {
    console.log('No WebSQL to clear');
  }
}

// 6. Clear any global Keycloak instances
console.log('🧹 Clearing global Keycloak...');
if (window.keycloak) {
  try {
    window.keycloak.logout();
  } catch (e) {
    console.log('Keycloak logout failed, continuing...');
  }
  delete window.keycloak;
}

// 7. Clear all auth-related globals
const authGlobals = [
  'keycloak', 'auth', 'token', 'user', 'access_token', 
  'refresh_token', 'id_token', 'kc-callback', 'kc-state'
];
authGlobals.forEach(key => {
  if (window[key]) {
    delete window[key];
    console.log(`✅ Cleared global: ${key}`);
  }
});

// 8. Clear any Vue/Pinia stores
if (window.__VUE_DEVTOOLS_GLOBAL_HOOK__) {
  try {
    window.__VUE_DEVTOOLS_GLOBAL_HOOK__.apps.forEach(app => {
      if (app._instance && app._instance.appContext) {
        const stores = app._instance.appContext.config.globalProperties;
        Object.keys(stores).forEach(key => {
          if (key.includes('auth') || key.includes('token')) {
            delete stores[key];
          }
        });
      }
    });
  } catch (e) {
    console.log('Vue store cleanup failed, continuing...');
  }
}

// 9. Force clear any pending requests
if (window.AbortController) {
  try {
    const controller = new AbortController();
    controller.abort();
  } catch (e) {
    console.log('Request abort failed, continuing...');
  }
}

// 10. Clear any timers/intervals
for (let i = 1; i < 10000; i++) {
  clearTimeout(i);
  clearInterval(i);
}

console.log('🎉 ULTRA cleanup completed!');
console.log('🔄 Reloading in 3 seconds...');

// Show countdown
let countdown = 3;
const countdownInterval = setInterval(() => {
  console.log(`⏰ Reloading in ${countdown}...`);
  countdown--;
  if (countdown <= 0) {
    clearInterval(countdownInterval);
    console.log('🚀 RELOADING NOW!');
    window.location.href = window.location.origin + window.location.pathname;
  }
}, 1000);
