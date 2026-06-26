<template>
  <footer class="app-footer">
    <div class="container">
      <div class="app-footer__content">
        <div class="app-footer__section">
          <h3 class="footer-title">关于我们</h3>
          <p class="footer-text">专业的体育馆预约与社交平台，为您提供便捷的预约服务和社交互动。</p>
        </div>
        <div class="app-footer__section">
          <h3 class="footer-title">快速链接</h3>
          <ul class="footer-links">
            <li><RouterLink to="/">首页</RouterLink></li>
            <li><RouterLink to="/venues">场馆列表</RouterLink></li>
            <li><RouterLink to="/about">关于我们</RouterLink></li>
          </ul>
        </div>
        <div class="app-footer__section">
          <h3 class="footer-title">联系我们</h3>
          <p class="footer-text">邮箱: 2787721585@qq.com</p>
          <p class="footer-text">电话: 15072093823</p>
        </div>
      </div>
      <div class="app-footer__copyright">
        <p>&copy; 2026 体育馆预约与社交平台. All rights reserved.</p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'
</script>

<style scoped>
.app-footer {
  background-color: var(--text-primary);
  color: #fff;
  padding: var(--spacing-xl) 0 var(--spacing-md);
  margin-top: auto;
}

.app-footer__content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-xl);
  margin-bottom: var(--spacing-xl);
}

.footer-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: var(--spacing-md);
}

.footer-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.8;
  margin-bottom: var(--spacing-sm);
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-links li {
  margin-bottom: var(--spacing-sm);
}

.footer-links a {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.footer-links a:hover {
  color: #fff;
}

.app-footer__copyright {
  text-align: center;
  padding-top: var(--spacing-md);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.app-footer__copyright p {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
}
</style>

