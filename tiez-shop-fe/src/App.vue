<script setup>
import { onMounted } from 'vue'
import Layout from './components/Layout.vue'
import { useAuthStore } from './stores/auth.js'

const authStore = useAuthStore()

onMounted(async () => {
  try {
    await authStore.initializeAuth()
  } catch (error) {
    console.error('App initialization error:', error)
    // Continue loading the app even if auth fails
  }
})
</script>

<template>
  <Layout />
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  line-height: 1.6;
  color: #2c3e50;
  background: #f8f9fa;
  margin: 0;
  padding: 0;
  width: 100%;
  overflow-x: hidden;
}

#app {
  min-height: 100vh;
  width: 100%;
}

a {
  color: inherit;
  text-decoration: none;
}

button {
  font-family: inherit;
}

input, select, textarea {
  font-family: inherit;
}

/* Scrollbar styling */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* Large screen optimizations */
@media (min-width: 1920px) {
  .container {
    padding: 0 40px;
  }
}

@media (min-width: 2560px) {
  .container {
    padding: 0 60px;
  }
}
</style>
