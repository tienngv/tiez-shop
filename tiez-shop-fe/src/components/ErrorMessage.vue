<template>
  <div class="error-container">
    <p class="error-message">{{ message }}</p>
    <button v-if="showRetry" @click="$emit('retry')" class="retry-btn">
      Thử lại
    </button>
  </div>
</template>

<script setup>
defineProps({
  message: {
    type: String,
    required: true
  },
  showRetry: {
    type: Boolean,
    default: true
  }
})

defineEmits(['retry'])
</script>

<style scoped>
.error-container {
  text-align: center;
  padding: 3rem;
}

.error-message {
  color: #e74c3c;
  margin-bottom: 1rem;
  font-size: 1.1rem;
}

.retry-btn {
  background: #3498db;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
  transition: background 0.3s;
}

.retry-btn:hover {
  background: #2980b9;
}
</style>
