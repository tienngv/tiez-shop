<template>
  <div class="cleanup-guide">
    <h3>🔧 Hướng dẫn khắc phục lỗi 401</h3>
    
    <div class="cleanup-steps">
      <div class="step">
        <h4>📋 Cách 1: Sử dụng Debug Panel</h4>
        <p>1. Vào trang Profile</p>
        <p>2. Click nút "Force Cleanup"</p>
        <p>3. Confirm để clear data</p>
        <p>4. Page sẽ tự động reload</p>
      </div>
      
      <div class="step">
        <h4>💻 Cách 2: Manual Console</h4>
        <p>1. Mở Developer Tools (F12)</p>
        <p>2. Vào tab Console</p>
        <p>3. Copy và paste script bên dưới:</p>
        <div class="code-block">
          <code>import('./src/utils/manual-cleanup.js')</code>
        </div>
        <button @click="copyScript" class="copy-btn">📋 Copy Script</button>
      </div>
      
      <div class="step">
        <h4>🔄 Cách 3: Hard Refresh</h4>
        <p>1. Nhấn Ctrl + Shift + R (Windows)</p>
        <p>2. Hoặc Cmd + Shift + R (Mac)</p>
        <p>3. Hoặc Ctrl + F5</p>
      </div>
      
      <div class="step">
        <h4>🌐 Cách 4: Clear Browser Data</h4>
        <p>1. Vào Settings > Privacy</p>
        <p>2. Clear browsing data</p>
        <p>3. Chọn "All time"</p>
        <p>4. Check "Cookies" và "Cached data"</p>
      </div>
    </div>
    
    <div class="troubleshooting">
      <h4>🚨 Nếu vẫn không được:</h4>
      <ul>
        <li>Kiểm tra Keycloak server có đang chạy không (localhost:8180)</li>
        <li>Kiểm tra realm "tiez-shop" có tồn tại không</li>
        <li>Kiểm tra client "tienngv" có được cấu hình đúng không</li>
        <li>Thử restart Keycloak server</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
const copyScript = () => {
  const script = `import('./src/utils/manual-cleanup.js')`;
  navigator.clipboard.writeText(script).then(() => {
    alert('Script đã được copy! Paste vào console và nhấn Enter');
  }).catch(() => {
    // Fallback for older browsers
    const textArea = document.createElement('textarea');
    textArea.value = script;
    document.body.appendChild(textArea);
    textArea.select();
    document.execCommand('copy');
    document.body.removeChild(textArea);
    alert('Script đã được copy! Paste vào console và nhấn Enter');
  });
};
</script>

<style scoped>
.cleanup-guide {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  padding: 1.5rem;
  margin: 1rem 0;
}

.cleanup-steps {
  display: grid;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.step {
  background: white;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  padding: 1rem;
}

.step h4 {
  color: #495057;
  margin-bottom: 0.5rem;
}

.step p {
  margin: 0.25rem 0;
  color: #6c757d;
}

.code-block {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 4px;
  padding: 0.5rem;
  margin: 0.5rem 0;
  font-family: 'Courier New', monospace;
}

.copy-btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 0.5rem;
}

.copy-btn:hover {
  background: #0056b3;
}

.troubleshooting {
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 6px;
  padding: 1rem;
}

.troubleshooting h4 {
  color: #856404;
  margin-bottom: 0.5rem;
}

.troubleshooting ul {
  margin: 0;
  padding-left: 1.5rem;
}

.troubleshooting li {
  color: #856404;
  margin: 0.25rem 0;
}
</style>
