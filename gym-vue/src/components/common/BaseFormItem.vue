<template>
  <div class="base-form-item">
    <label v-if="label" class="base-form-item__label" :style="{ width: labelWidth }">
      {{ label }}
      <span v-if="required" class="base-form-item__required">*</span>
    </label>
    <div class="base-form-item__content" :style="{ marginLeft: label ? '12px' : '0' }">
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  label?: string
  labelWidth?: string
  required?: boolean
}

withDefaults(defineProps<Props>(), {
  labelWidth: '100px',
  required: false,
})
</script>

<style scoped>
.base-form-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: var(--spacing-lg);
}

.base-form-item__label {
  flex-shrink: 0;
  padding-top: 10px;
  font-size: 14px;
  color: var(--text-primary);
  text-align: right;
}

.base-form-item__required {
  color: var(--danger-color);
  margin-left: 4px;
}

.base-form-item__content {
  flex: 1;
}
</style>

