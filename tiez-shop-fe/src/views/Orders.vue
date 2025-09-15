<template>
  <div class="orders">
    <div class="orders-header">
      <h1>Đơn hàng của tôi</h1>
      <div class="orders-filter">
        <select v-model="selectedStatus" class="status-filter">
          <option value="">Tất cả trạng thái</option>
          <option value="pending">Chờ xử lý</option>
          <option value="processing">Đang xử lý</option>
          <option value="shipped">Đã giao hàng</option>
          <option value="delivered">Đã nhận hàng</option>
          <option value="cancelled">Đã hủy</option>
        </select>
      </div>
    </div>

    <div class="orders-content">
      <div v-if="loading" class="loading">
        <p>Đang tải...</p>
      </div>

      <div v-else-if="filteredOrders.length === 0" class="no-orders">
        <div class="no-orders-content">
          <div class="no-orders-icon">📦</div>
          <h2>Chưa có đơn hàng nào</h2>
          <p>Bạn chưa có đơn hàng nào. Hãy bắt đầu mua sắm!</p>
          <router-link to="/products" class="start-shopping-btn">
            Bắt đầu mua sắm
          </router-link>
        </div>
      </div>

      <div v-else class="orders-list">
        <div v-for="order in filteredOrders" :key="order.id" class="order-card">
          <div class="order-header">
            <div class="order-info">
              <h3>Đơn hàng #{{ order.id }}</h3>
              <p class="order-date">{{ formatDate(order.createdAt) }}</p>
            </div>
            <div class="order-status">
              <span :class="['status-badge', order.status]">
                {{ getStatusText(order.status) }}
              </span>
            </div>
          </div>

          <div class="order-items">
            <div v-for="item in order.items" :key="item.id" class="order-item">
              <img :src="item.image" :alt="item.name" class="item-image">
              <div class="item-details">
                <h4 class="item-name">{{ item.name }}</h4>
                <div class="item-quantity">Số lượng: {{ item.quantity }}</div>
                <div class="item-price">{{ formatPrice(item.price) }}</div>
              </div>
            </div>
          </div>

          <div class="order-summary">
            <div class="summary-row">
              <span>Tạm tính:</span>
              <span>{{ formatPrice(order.subtotal) }}</span>
            </div>
            <div class="summary-row">
              <span>Phí vận chuyển:</span>
              <span>{{ order.shippingFee > 0 ? formatPrice(order.shippingFee) : 'Miễn phí' }}</span>
            </div>
            <div class="summary-row total">
              <span>Tổng cộng:</span>
              <span>{{ formatPrice(order.total) }}</span>
            </div>
          </div>

          <div class="order-actions">
            <button @click="viewOrderDetail(order)" class="view-detail-btn">
              Xem chi tiết
            </button>
            <button v-if="order.status === 'pending'" @click="cancelOrder(order.id)" class="cancel-btn">
              Hủy đơn hàng
            </button>
            <button v-if="order.status === 'delivered'" @click="reorder(order)" class="reorder-btn">
              Mua lại
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth.js'
import { userApi } from '../services/api.js'

const authStore = useAuthStore()
const loading = ref(true)
const selectedStatus = ref('')
const error = ref('')

// Mock data - trong thực tế sẽ lấy từ API
const orders = ref([
  {
    id: 'ORD001',
    status: 'delivered',
    createdAt: '2024-01-15T10:30:00Z',
    items: [
      {
        id: 1,
        name: 'iPhone 15 Pro',
        price: 29990000,
        quantity: 1,
        image: 'https://via.placeholder.com/100x100?text=iPhone+15+Pro'
      }
    ],
    subtotal: 29990000,
    shippingFee: 0,
    total: 29990000
  },
  {
    id: 'ORD002',
    status: 'shipped',
    createdAt: '2024-01-20T14:15:00Z',
    items: [
      {
        id: 2,
        name: 'Samsung Galaxy S24',
        price: 24990000,
        quantity: 1,
        image: 'https://via.placeholder.com/100x100?text=Samsung+Galaxy+S24'
      },
      {
        id: 5,
        name: 'Áo thun nam',
        price: 299000,
        quantity: 2,
        image: 'https://via.placeholder.com/100x100?text=Áo+thun+nam'
      }
    ],
    subtotal: 25588000,
    shippingFee: 0,
    total: 25588000
  },
  {
    id: 'ORD003',
    status: 'processing',
    createdAt: '2024-01-25T09:45:00Z',
    items: [
      {
        id: 7,
        name: 'Sách lập trình',
        price: 199000,
        quantity: 3,
        image: 'https://via.placeholder.com/100x100?text=Sách+lập+trình'
      }
    ],
    subtotal: 597000,
    shippingFee: 30000,
    total: 627000
  },
  {
    id: 'ORD004',
    status: 'pending',
    createdAt: '2024-01-28T16:20:00Z',
    items: [
      {
        id: 8,
        name: 'Máy xay sinh tố',
        price: 1299000,
        quantity: 1,
        image: 'https://via.placeholder.com/100x100?text=Máy+xay+sinh+tố'
      }
    ],
    subtotal: 1299000,
    shippingFee: 0,
    total: 1299000
  }
])

const filteredOrders = computed(() => {
  if (!selectedStatus.value) {
    return orders.value
  }
  return orders.value.filter(order => order.status === selectedStatus.value)
})

const loadUserOrders = async () => {
  if (!authStore.userId) {
    error.value = 'Không tìm thấy thông tin user'
    loading.value = false
    return
  }

  try {
    // Gọi API để lấy đơn hàng của user
    const ordersData = await userApi.getUserOrders(authStore.userId)
    orders.value = ordersData || []
  } catch (err) {
    console.error('Error loading user orders:', err)
    error.value = 'Không thể tải danh sách đơn hàng. Vui lòng thử lại.'
    // Giữ mock data để demo
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadUserOrders()
})

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('vi-VN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price)
}

const getStatusText = (status) => {
  const statusMap = {
    pending: 'Chờ xử lý',
    processing: 'Đang xử lý',
    shipped: 'Đã giao hàng',
    delivered: 'Đã nhận hàng',
    cancelled: 'Đã hủy'
  }
  return statusMap[status] || status
}

const viewOrderDetail = (order) => {
  // Trong thực tế sẽ chuyển đến trang chi tiết đơn hàng
  alert(`Xem chi tiết đơn hàng ${order.id}`)
}

const cancelOrder = (orderId) => {
  if (confirm('Bạn có chắc chắn muốn hủy đơn hàng này?')) {
    // Trong thực tế sẽ gọi API để hủy đơn hàng
    alert(`Đơn hàng ${orderId} đã được hủy`)
  }
}

const reorder = (order) => {
  // Trong thực tế sẽ thêm các sản phẩm vào giỏ hàng
  alert('Các sản phẩm đã được thêm vào giỏ hàng!')
}
</script>

<style scoped>
.orders {
  padding: 2rem 20px;
  width: 100%;
  margin: 0 auto;
}

.orders-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.orders-header h1 {
  color: #2c3e50;
}

.orders-filter {
  display: flex;
  gap: 1rem;
}

.status-filter {
  padding: 0.75rem;
  border: 2px solid #ecf0f1;
  border-radius: 8px;
  font-size: 1rem;
  background: white;
}

.status-filter:focus {
  outline: none;
  border-color: #3498db;
}

.loading {
  text-align: center;
  padding: 3rem;
  color: #7f8c8d;
}

.no-orders {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.no-orders-content {
  text-align: center;
}

.no-orders-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.no-orders-content h2 {
  color: #2c3e50;
  margin-bottom: 1rem;
}

.no-orders-content p {
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

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.order-card {
  background: white;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  background: #f8f9fa;
  border-bottom: 1px solid #ecf0f1;
}

.order-info h3 {
  color: #2c3e50;
  margin: 0 0 0.5rem 0;
}

.order-date {
  color: #7f8c8d;
  margin: 0;
  font-size: 0.9rem;
}

.status-badge {
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: bold;
  text-transform: uppercase;
}

.status-badge.pending {
  background: #f39c12;
  color: white;
}

.status-badge.processing {
  background: #3498db;
  color: white;
}

.status-badge.shipped {
  background: #9b59b6;
  color: white;
}

.status-badge.delivered {
  background: #27ae60;
  color: white;
}

.status-badge.cancelled {
  background: #e74c3c;
  color: white;
}

.order-items {
  padding: 1.5rem;
}

.order-item {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #ecf0f1;
}

.order-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
}

.item-details {
  flex: 1;
}

.item-name {
  color: #2c3e50;
  margin: 0 0 0.5rem 0;
  font-size: 1rem;
}

.item-quantity {
  color: #7f8c8d;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}

.item-price {
  color: #e74c3c;
  font-weight: bold;
}

.order-summary {
  padding: 1.5rem;
  background: #f8f9fa;
  border-top: 1px solid #ecf0f1;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
  color: #2c3e50;
}

.summary-row.total {
  font-weight: bold;
  font-size: 1.1rem;
  border-top: 1px solid #ecf0f1;
  padding-top: 0.5rem;
  margin-top: 0.5rem;
}

.order-actions {
  padding: 1.5rem;
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
}

.view-detail-btn,
.cancel-btn,
.reorder-btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
}

.view-detail-btn {
  background: #3498db;
  color: white;
}

.view-detail-btn:hover {
  background: #2980b9;
}

.cancel-btn {
  background: #e74c3c;
  color: white;
}

.cancel-btn:hover {
  background: #c0392b;
}

.reorder-btn {
  background: #27ae60;
  color: white;
}

.reorder-btn:hover {
  background: #229954;
}

@media (max-width: 768px) {
  .orders-header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .order-header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .order-actions {
    flex-direction: column;
  }
  
  .order-item {
    flex-direction: column;
    text-align: center;
  }
  
  .item-image {
    align-self: center;
  }
}
</style>
