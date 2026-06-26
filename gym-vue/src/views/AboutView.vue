<template>
  <div class="about-view page-container">
    <div class="container">
      <h1 class="page-title">关于我们</h1>

      <div class="about-content">
        <section class="about-section">
          <h2>项目简介</h2>
          <p>
            体育馆预约与社交平台是一个基于Vue 3 + TypeScript + Pinia的现代化Web应用，
            旨在为用户提供便捷的体育馆预约服务和社交互动功能。系统采用前后端分离架构，
            前端负责用户界面展示和交互，后端提供API接口支持。
          </p>
        </section>

        <section class="about-section">
          <h2>技术栈</h2>
          <ul class="tech-list">
            <li><strong>框架:</strong> Vue 3.5.25 (Composition API)</li>
            <li><strong>语言:</strong> TypeScript 5.9.0</li>
            <li><strong>构建工具:</strong> Vite 7.2.4</li>
            <li><strong>状态管理:</strong> Pinia 3.0.4</li>
            <li><strong>路由:</strong> Vue Router 4.6.3</li>
            <li><strong>UI组件库:</strong> Element Plus 2.11.8</li>
          </ul>
        </section>

        <section class="about-section">
          <h2>核心功能</h2>
          <div class="features-grid">
            <div class="feature-item">
              <div class="feature-icon">👤</div>
              <h3>用户认证</h3>
              <p>支持用户注册、登录、登出等功能</p>
            </div>
            <div class="feature-item">
              <div class="feature-icon">🏋️</div>
              <h3>场馆展示</h3>
              <p>展示场馆列表、详情、筛选和搜索功能</p>
            </div>
            <div class="feature-item">
              <div class="feature-icon">📅</div>
              <h3>场馆预约</h3>
              <p>实现场馆预约、取消预约、查看预约记录</p>
            </div>
            <div class="feature-item">
              <div class="feature-icon">👥</div>
              <h3>个人中心</h3>
              <p>管理个人信息和预约记录</p>
            </div>
          </div>
        </section>

        <section class="about-section">
          <h2>联系我们</h2>
          <div class="contact-info">
            <p><strong>邮箱:</strong> 2787721585@qq.com</p>
            <p><strong>电话:</strong> 15072093823</p>
            <p><strong>地址:</strong> 孝感市孝南区xxx路205号</p>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// About page
</script>

<style scoped>
.page-title {
  font-size: 32px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-xl);
  text-align: center;
}

.about-content {
  max-width: 900px;
  margin: 0 auto;
}

.about-section {
  margin-bottom: var(--spacing-xl);
  padding: var(--spacing-lg);
  background-color: var(--bg-color);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
}

.about-section h2 {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
  padding-bottom: var(--spacing-md);
  border-bottom: 2px solid var(--primary-color);
}

.about-section p {
  font-size: 16px;
  color: var(--text-regular);
  line-height: 1.8;
  margin-bottom: var(--spacing-md);
}

.tech-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tech-list li {
  padding: var(--spacing-sm) 0;
  font-size: 16px;
  color: var(--text-regular);
  border-bottom: 1px solid var(--border-lighter);
}

.tech-list li:last-child {
  border-bottom: none;
}

.tech-list strong {
  color: var(--text-primary);
  font-weight: 600;
  margin-right: var(--spacing-sm);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-lg);
  margin-top: var(--spacing-lg);
}

.feature-item {
  text-align: center;
  padding: var(--spacing-md);
}

.feature-icon {
  font-size: 48px;
  margin-bottom: var(--spacing-md);
}

.feature-item h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
}

.feature-item p {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.contact-info {
  margin-top: var(--spacing-md);
}

.contact-info p {
  font-size: 16px;
  color: var(--text-regular);
  margin-bottom: var(--spacing-sm);
}

.contact-info strong {
  color: var(--text-primary);
  font-weight: 600;
  margin-right: var(--spacing-sm);
}
</style>
