<template>
  <div class="app-layout">
    <!-- Header -->
    <header class="header">
      <div class="container">
        <div class="header-content">
          <div class="logo">
            <router-link to="/">
              <h1>TiezShop</h1>
            </router-link>
          </div>
          
          <nav class="nav">
            <router-link to="/" class="nav-link">Trang chủ</router-link>
            <router-link to="/products" class="nav-link">Sản phẩm</router-link>
            <div class="nav-dropdown">
              <span class="nav-link dropdown-toggle">Thương hiệu</span>
              <div class="dropdown-menu">
                <router-link to="/products?brand=nike" class="dropdown-item">Nike</router-link>
                <router-link to="/products?brand=adidas" class="dropdown-item">Adidas</router-link>
                <router-link to="/products?brand=puma" class="dropdown-item">Puma</router-link>
                <router-link to="/products?brand=converse" class="dropdown-item">Converse</router-link>
                <router-link to="/products?brand=vans" class="dropdown-item">Vans</router-link>
              </div>
            </div>
          </nav>
          
          <div class="header-actions">
            <router-link to="/cart" class="cart-link">
              <i class="cart-icon">🛒</i>
              <span v-if="cartStore.totalItems > 0" class="cart-count">
                {{ cartStore.totalItems }}
              </span>
            </router-link>
            
            <div v-if="authStore.isAuthenticated" class="user-menu">
              <router-link to="/profile" class="user-link">
                <i class="user-icon">👤</i>
                {{ authStore.userInfo?.name || 'User' }}
              </router-link>
              <button @click="handleLogout" class="logout-btn">Đăng xuất</button>
            </div>
            
            <div v-else class="auth-buttons">
              <button @click="handleLogin" class="login-btn">Đăng nhập</button>
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="main-content">
      <div class="container">
        <router-view />
      </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-section">
            <h3>TiezShop</h3>
            <p>Shop thời trang chuyên nghiệp với các thương hiệu nổi tiếng</p>
          </div>
          <div class="footer-section">
            <h4>Danh mục</h4>
            <ul>
              <li><router-link to="/products?category=shoes">Giày dép</router-link></li>
              <li><router-link to="/products?category=clothing">Quần áo</router-link></li>
              <li><router-link to="/products?category=accessories">Phụ kiện</router-link></li>
              <li><router-link to="/products?category=bags">Túi xách</router-link></li>
            </ul>
          </div>
          <div class="footer-section">
            <h4>Thương hiệu</h4>
            <ul>
              <li><router-link to="/products?brand=nike">Nike</router-link></li>
              <li><router-link to="/products?brand=adidas">Adidas</router-link></li>
              <li><router-link to="/products?brand=puma">Puma</router-link></li>
              <li><router-link to="/products?brand=converse">Converse</router-link></li>
            </ul>
          </div>
          <div class="footer-section">
            <h4>Hỗ trợ</h4>
            <ul>
              <li><a href="#">Liên hệ</a></li>
              <li><a href="#">Hướng dẫn mua hàng</a></li>
              <li><a href="#">Chính sách đổi trả</a></li>
              <li><a href="#">Bảo hành</a></li>
            </ul>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2024 TiezShop. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { useAuthStore } from '../stores/auth.js'
import { useCartStore } from '../stores/cart.js'
import { login, logout } from '../services/keycloak.js'

const authStore = useAuthStore()
const cartStore = useCartStore()

const handleLogin = () => {
  login()
}

const handleLogout = () => {
  logout()
}
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.container {
  width: 100%;
  margin: 0 auto;
  padding: 0 20px;
}

/* Header */
.header {
  background: #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 0;
}

.logo h1 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.5rem;
}

.logo a {
  text-decoration: none;
}

.nav {
  display: flex;
  gap: 2rem;
}

.nav-link {
  text-decoration: none;
  color: #2c3e50;
  font-weight: 500;
  transition: color 0.3s;
}

.nav-link:hover,
.nav-link.router-link-active {
  color: #3498db;
}

.nav-dropdown {
  position: relative;
}

.dropdown-toggle {
  cursor: pointer;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  min-width: 150px;
  opacity: 0;
  visibility: hidden;
  transform: translateY(-10px);
  transition: all 0.3s;
  z-index: 1000;
}

.nav-dropdown:hover .dropdown-menu {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.dropdown-item {
  display: block;
  padding: 0.75rem 1rem;
  color: #2c3e50;
  text-decoration: none;
  transition: background 0.3s;
  border-bottom: 1px solid #ecf0f1;
}

.dropdown-item:last-child {
  border-bottom: none;
}

.dropdown-item:hover {
  background: #f8f9fa;
  color: #3498db;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.cart-link {
  position: relative;
  text-decoration: none;
  color: #2c3e50;
  font-size: 1.2rem;
}

.cart-count {
  position: absolute;
  top: -8px;
  right: -8px;
  background: #e74c3c;
  color: white;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-weight: bold;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.user-link {
  text-decoration: none;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.logout-btn,
.login-btn {
  background: #3498db;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
}

.logout-btn:hover,
.login-btn:hover {
  background: #2980b9;
}

/* Main Content */
.main-content {
  flex: 1;
  padding: 2rem 0;
}

/* Footer */
.footer {
  background: #2c3e50;
  color: white;
  margin-top: auto;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  padding: 2rem 0;
}

.footer-section h3,
.footer-section h4 {
  margin: 0 0 1rem 0;
}

.footer-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-section ul li {
  margin-bottom: 0.5rem;
}

.footer-section a {
  color: #bdc3c7;
  text-decoration: none;
  transition: color 0.3s;
}

.footer-section a:hover {
  color: white;
}

.footer-bottom {
  border-top: 1px solid #34495e;
  padding: 1rem 0;
  text-align: center;
  color: #bdc3c7;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 1rem;
  }
  
  .nav {
    gap: 1rem;
  }
  
  .header-actions {
    gap: 0.5rem;
  }
}
</style>
