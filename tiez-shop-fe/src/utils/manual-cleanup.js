// Manual cleanup script - Copy and paste this into browser console
// This will completely reset Keycloak authentication

(function() {
  console.log('🔥 MANUAL ULTRA CLEANUP STARTING...');
  
  // Step 1: Clear all storage
  console.log('🧹 Step 1: Clearing all storage...');
  localStorage.clear();
  sessionStorage.clear();
  console.log('✅ Storage cleared');
  
  // Step 2: Clear all cookies
  console.log('🧹 Step 2: Clearing all cookies...');
  document.cookie.split(";").forEach(function(c) { 
    const cookie = c.replace(/^ +/, "").split("=")[0];
    document.cookie = cookie + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/";
    document.cookie = cookie + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/;domain=localhost";
    document.cookie = cookie + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/;domain=.localhost";
  });
  console.log('✅ Cookies cleared');
  
  // Step 3: Clear IndexedDB
  console.log('🧹 Step 3: Clearing IndexedDB...');
  if ('indexedDB' in window) {
    indexedDB.databases().then(databases => {
      databases.forEach(db => {
        indexedDB.deleteDatabase(db.name);
        console.log(`✅ Deleted DB: ${db.name}`);
      });
    }).catch(err => console.log('No IndexedDB'));
  }
  
  // Step 4: Clear global variables
  console.log('🧹 Step 4: Clearing global variables...');
  const globals = ['keycloak', 'auth', 'token', 'user'];
  globals.forEach(key => {
    if (window[key]) {
      delete window[key];
      console.log(`✅ Cleared global: ${key}`);
    }
  });
  
  // Step 5: Clear any timers
  console.log('🧹 Step 5: Clearing timers...');
  for (let i = 1; i < 10000; i++) {
    clearTimeout(i);
    clearInterval(i);
  }
  console.log('✅ Timers cleared');
  
  console.log('🎉 MANUAL CLEANUP COMPLETED!');
  console.log('🔄 Please reload the page now');
  
  // Auto reload after 2 seconds
  setTimeout(() => {
    console.log('🚀 RELOADING...');
    window.location.reload();
  }, 2000);
})();
