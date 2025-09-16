import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'

// Import components
import Home from '../views/Home.vue'
import Register from '../views/Register.vue'
import Callback from '../views/Callback.vue'
import Logout from '../views/Logout.vue'
import Products from '../views/Products.vue'
import ProductDetail from '../views/ProductDetail.vue'
import Cart from '../views/Cart.vue'
import Profile from '../views/Profile.vue'
import Orders from '../views/Orders.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/callback',
    name: 'Callback',
    component: Callback
  },
  {
    path: '/logout',
    name: 'Logout',
    component: Logout
  },
  {
    path: '/products',
    name: 'Products',
    component: Products
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: ProductDetail,
    props: true
  },
  {
    path: '/cart',
    name: 'Cart',
    component: Cart,
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: { requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: Orders,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    // Redirect directly to Keycloak instead of login page
    const keycloakUrl = `http://localhost:8180/realms/tiez-shop/protocol/openid-connect/auth?` +
      `client_id=tienngv&` +
      `redirect_uri=${encodeURIComponent('http://localhost:5173/callback')}&` +
      `response_type=code&` +
      `scope=openid`
    
    window.location.href = keycloakUrl
  } else {
    next()
  }
})

export default router
