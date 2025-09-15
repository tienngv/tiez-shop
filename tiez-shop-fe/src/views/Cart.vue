<template>
  <div class="cart">
    <div class="cart-header">
      <h1>Giỏ hàng của bạn</h1>
      <p v-if="cartStore.isEmpty" class="empty-cart-message">
        Giỏ hàng của bạn đang trống
      </p>
    </div>

    <div v-if="!cartStore.isEmpty" class="cart-content">
      <div class="cart-items">
        <div v-for="item in cartStore.items" :key="item.id" class="cart-item">
          <div class="item-image">
            <img :src="item.image" :alt="item.name">
          </div>
          
          <div class="item-details">
            <h3 class="item-name">{{ item.name }}</h3>
            <div class="item-price">{{ formatPrice(item.price) }}</div>
          </div>
          
          <div class="item-quantity">
            <label>Số lượng:</label>
            <div class="quantity-controls">
              <button @click="updateQuantity(item.id, item.quantity - 1)" class="quantity-btn">-</button>
              <input 
                v-model.number="item.quantity" 
                type="number" 
                min="1" 
                class="quantity-input"
                @change="updateQuantity(item.id, item.quantity)"
              >
              <button @click="updateQuantity(item.id, item.quantity + 1)" class="quantity-btn">+</button>
            </div>
          </div>
          
          <div class="item-total">
            <div class="total-price">{{ formatPrice(item.price * item.quantity) }}</div>
          </div>
          
          <div class="item-actions">
            <button @click="removeItem(item.id)" class="remove-btn">
              🗑️
            </button>
          </div>
        </div>
      </div>

      <div class="cart-summary">
        <div class="summary-card">
          <h3>Tóm tắt đơn hàng</h3>
          
          <div class="summary-row">
            <span>Tạm tính:</span>
            <span>{{ formatPrice(cartStore.totalPrice) }}</span>
          </div>
          
          <div class="summary-row">
            <span>Phí vận chuyển:</span>
            <span class="free-shipping">Miễn phí</span>
          </div>
          
          <div class="summary-row total">
            <span>Tổng cộng:</span>
            <span>{{ formatPrice(cartStore.totalPrice) }}</span>
          </div>
          
          <div class="checkout-actions">
            <button @click="proceedToCheckout" class="checkout-btn">
              Thanh toán
            </button>
            <router-link to="/products" class="continue-shopping">
              Tiếp tục mua sắm
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <div v-if="cartStore.isEmpty" class="empty-cart">
      <div class="empty-cart-content">
        <div class="empty-cart-icon">🛒</div>
        <h2>Giỏ hàng trống</h2>
        <p>Hãy thêm một số sản phẩm vào giỏ hàng để bắt đầu mua sắm!</p>
        <router-link to="/products" class="start-shopping-btn">
          Bắt đầu mua sắm
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useCartStore } from '../stores/cart.js'
import { useRouter } from 'vue-router'

const cartStore = useCartStore()
const router = useRouter()

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price)
}

const updateQuantity = (productId, quantity) => {
  cartStore.updateQuantity(productId, quantity)
}

const removeItem = (productId) => {
  cartStore.removeFromCart(productId)
}

const proceedToCheckout = () => {
  // Trong thực tế sẽ chuyển đến trang thanh toán
  alert('Chức năng thanh toán sẽ được triển khai sau!')
}
</script>

<style scoped>
.cart {
  padding: 2rem 20px;
  width: 100%;
  margin: 0 auto;
}

.cart-header {
  text-align: center;
  margin-bottom: 2rem;
}

.cart-header h1 {
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.empty-cart-message {
  color: #7f8c8d;
  font-size: 1.1rem;
}

.cart-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 2rem;
  max-width: 1400px;
  margin: 0 auto;
}

.cart-items {
  background: white;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  overflow: hidden;
}

.cart-item {
  display: grid;
  grid-template-columns: 100px 1fr auto auto auto;
  gap: 1rem;
  padding: 1.5rem;
  border-bottom: 1px solid #ecf0f1;
  align-items: center;
}

.cart-item:last-child {
  border-bottom: none;
}

.item-image img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
}

.item-details {
  min-width: 0;
}

.item-name {
  color: #2c3e50;
  margin: 0 0 0.5rem 0;
  font-size: 1.1rem;
}

.item-price {
  color: #e74c3c;
  font-weight: bold;
  font-size: 1.1rem;
}

.item-quantity {
  text-align: center;
}

.item-quantity label {
  display: block;
  margin-bottom: 0.5rem;
  color: #2c3e50;
  font-size: 0.9rem;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.quantity-btn {
  background: #3498db;
  color: white;
  border: none;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1rem;
  font-weight: bold;
  transition: background 0.3s;
}

.quantity-btn:hover {
  background: #2980b9;
}

.quantity-input {
  width: 60px;
  height: 30px;
  text-align: center;
  border: 2px solid #ecf0f1;
  border-radius: 4px;
  font-size: 0.9rem;
}

.item-total {
  text-align: right;
}

.total-price {
  color: #2c3e50;
  font-weight: bold;
  font-size: 1.1rem;
}

.item-actions {
  text-align: center;
}

.remove-btn {
  background: #e74c3c;
  color: white;
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1.2rem;
  transition: background 0.3s;
}

.remove-btn:hover {
  background: #c0392b;
}

.cart-summary {
  position: sticky;
  top: 2rem;
}

.summary-card {
  background: white;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  padding: 2rem;
}

.summary-card h3 {
  color: #2c3e50;
  margin-bottom: 1.5rem;
  text-align: center;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
  color: #2c3e50;
}

.summary-row.total {
  border-top: 2px solid #ecf0f1;
  padding-top: 1rem;
  font-weight: bold;
  font-size: 1.1rem;
}

.free-shipping {
  color: #27ae60;
  font-weight: bold;
}

.checkout-actions {
  margin-top: 2rem;
}

.checkout-btn {
  background: #27ae60;
  color: white;
  border: none;
  padding: 1rem 2rem;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  width: 100%;
  margin-bottom: 1rem;
  transition: background 0.3s;
}

.checkout-btn:hover {
  background: #229954;
}

.continue-shopping {
  display: block;
  text-align: center;
  color: #3498db;
  text-decoration: none;
  font-weight: bold;
  transition: color 0.3s;
}

.continue-shopping:hover {
  color: #2980b9;
}

.empty-cart {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.empty-cart-content {
  text-align: center;
}

.empty-cart-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.empty-cart-content h2 {
  color: #2c3e50;
  margin-bottom: 1rem;
}

.empty-cart-content p {
  color: #7f8c8d;
  margin-bottom: 2rem;
}

.start-shopping-btn {
  background: #3498db;
  color: white;
  padding: 1rem 2rem;
  border-radius: 8px;
  text-decoration: none;
  font-weight: bold;
  transition: background 0.3s;
}

.start-shopping-btn:hover {
  background: #2980b9;
}

@media (max-width: 768px) {
  .cart-content {
    grid-template-columns: 1fr;
  }
  
  .cart-item {
    grid-template-columns: 1fr;
    gap: 1rem;
    text-align: center;
  }
  
  .item-image {
    justify-self: center;
  }
  
  .quantity-controls {
    justify-content: center;
  }
}
</style>
