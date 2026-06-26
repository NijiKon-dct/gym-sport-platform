<template>
  <div class="admin-schools page-container">
    <div class="container">
      <div class="page-header">
        <div>
          <h1 class="page-title">学校列表管理</h1>
          <p class="page-subtitle">控制注册页下拉框里可选的学校数量与名称</p>
        </div>
        <div class="header-actions">
          <BaseButton type="secondary" @click="resetToDefault">恢复默认</BaseButton>
        </div>
      </div>

      <div class="card">
        <div class="add-row">
          <BaseInput
            v-model="newSchool"
            placeholder="输入学校名称"
            @keyup.enter="handleAdd"
          />
          <BaseButton type="primary" @click="handleAdd">添加学校</BaseButton>
        </div>
        <div class="tip">当前可选学校：{{ schools.length }} 所</div>

        <div v-if="schools.length === 0" class="empty-state">暂无学校，请添加</div>
        <ul v-else class="school-list">
          <li v-for="school in schools" :key="school" class="school-item">
            <span class="school-name">{{ school }}</span>
            <BaseButton type="danger" size="small" @click="removeSchool(school)">删除</BaseButton>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useSchoolStore } from '@/stores/school'
import { BaseButton, BaseInput } from '@/components/common'

const schoolStore = useSchoolStore()
const newSchool = ref('')

const schools = computed(() => schoolStore.schools)

onMounted(async () => {
  try {
    await schoolStore.fetchSchoolOptions()
  } catch (error) {
    console.error('加载学校列表失败', error)
  }
})

const handleAdd = async () => {
  if (!newSchool.value.trim()) {
    alert('请输入学校名称')
    return
  }
  try {
    const ok = await schoolStore.addSchool(newSchool.value)
    if (!ok) {
      alert('学校已存在或名称无效')
      return
    }
    newSchool.value = ''
  } catch (error: any) {
    alert(error?.message || '添加学校失败')
  }
}

const removeSchool = async (name: string) => {
  if (confirm(`确定删除「${name}」吗？`)) {
    try {
      await schoolStore.removeSchool(name)
    } catch (error: any) {
      alert(error?.message || '删除学校失败')
    }
  }
}

const resetToDefault = async () => {
  if (!confirm('确定恢复到默认学校列表吗？此操作会覆盖当前列表。')) {
    return
  }
  try {
    await schoolStore.resetToDefault()
  } catch (error: any) {
    alert(error?.message || '恢复默认学校列表失败')
  }
}
</script>

<style scoped>
.admin-schools {
  padding: var(--spacing-xl) 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
  flex-wrap: wrap;
  gap: var(--spacing-md);
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0;
  color: var(--text-primary);
  background: linear-gradient(135deg, var(--primary-color), #66b1ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  position: relative;
  padding-bottom: var(--spacing-sm);
}

.page-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, var(--primary-color), #66b1ff);
  border-radius: 2px;
}

.page-subtitle {
  margin: 8px 0 0;
  color: var(--text-secondary);
}

.header-actions {
  display: flex;
  gap: var(--spacing-sm);
}

.card {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-lighter);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  padding: var(--spacing-lg);
  transition: all 0.3s ease;
}

.card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.add-row {
  display: flex;
  gap: var(--spacing-md);
  flex-wrap: wrap;
  margin-bottom: var(--spacing-md);
}

.add-row .base-input {
  flex: 1;
  min-width: 240px;
}

.tip {
  color: var(--text-secondary);
  margin-bottom: var(--spacing-md);
}

.school-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.school-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md) var(--spacing-lg);
  border: 1px solid var(--border-lighter);
  border-radius: var(--border-radius-md);
  background: linear-gradient(90deg, rgba(248, 249, 250, 0.8), rgba(233, 236, 239, 0.5));
  transition: all 0.3s ease;
}

.school-item:hover {
  background: linear-gradient(90deg, rgba(64, 158, 255, 0.05), rgba(102, 177, 255, 0.02));
  border-color: var(--primary-color);
  transform: translateX(4px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.school-name {
  font-weight: 600;
  color: var(--text-primary);
}

.empty-state {
  text-align: center;
  color: var(--text-secondary);
  padding: var(--spacing-lg);
  font-size: 16px;
  background: linear-gradient(135deg, rgba(248, 249, 250, 0.5), rgba(233, 236, 239, 0.3));
  border-radius: var(--border-radius-md);
  border: 2px dashed var(--border-light);
}
</style>




