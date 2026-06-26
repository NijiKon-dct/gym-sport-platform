<template>
  <div class="base-select-wrapper">
    <div class="select-container">
      <select
        :value="modelValue"
        @change="handleChange"
        :class="['base-select', { 'has-error': error }]"
        :disabled="disabled"
      >
        <slot></slot>
      </select>
      <div class="select-arrow">▼</div>
    </div>
    <span v-if="error" class="error-message">{{ error }}</span>
  </div>
</template>

<script setup lang="ts">
interface Props {
  modelValue: string | number
  error?: string
  disabled?: boolean
  placeholder?: string
}

const props = withDefaults(defineProps<Props>(), {
  error: '',
  disabled: false,
  placeholder: '',
})

const emit = defineEmits<{
  'update:modelValue': [value: string | number]
}>()

const handleChange = (event: Event) => {
  const target = event.target as HTMLSelectElement
  const value = target.value
  // 如果原始值是数字类型，尝试转换为数字
  if (typeof props.modelValue === 'number') {
    const numValue = Number(value)
    emit('update:modelValue', isNaN(numValue) ? value : numValue)
  } else {
    emit('update:modelValue', value)
  }
}
</script>

<style scoped>
.base-select-wrapper {
  width: 100%;
  position: relative;
}

.select-container {
  position: relative;
  display: inline-block;
  width: 100%;
}

.base-select {
  width: 100%;
  padding: 10px 40px 10px 15px;
  border: 1px solid var(--border-base);
  border-radius: var(--border-radius-md);
  font-size: 14px;
  color: var(--text-primary);
  background-color: var(--bg-color);
  cursor: pointer;
  outline: none;
  transition: all 0.2s ease;
  font-family: inherit;
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  background-image: none;
}

/* 自定义下拉箭头 */
.select-arrow {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
  color: var(--text-secondary);
  font-size: 10px;
  transition: all 0.2s ease;
}

.base-select:hover:not(:disabled) {
  border-color: var(--primary-color);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.base-select:hover:not(:disabled) + .select-arrow {
  color: var(--primary-color);
  transform: translateY(-50%) scale(1.1);
}

.base-select:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.15);
}

.base-select:focus + .select-arrow {
  color: var(--primary-color);
  transform: translateY(-50%) rotate(180deg);
}

.base-select:disabled {
  background-color: var(--bg-secondary);
  cursor: not-allowed;
  opacity: 0.6;
}

.base-select:disabled + .select-arrow {
  opacity: 0.6;
  cursor: not-allowed;
}

.base-select.has-error {
  border-color: var(--danger-color, #f56c6c);
}

.base-select.has-error + .select-arrow {
  color: var(--danger-color, #f56c6c);
}

/* 美化下拉列表（Firefox 支持） */
.base-select optgroup {
  background-color: var(--bg-color);
  color: var(--text-primary);
  font-weight: 600;
  padding: 8px 10px;
  font-size: 13px;
}

.base-select option {
  background-color: var(--bg-color);
  color: var(--text-primary);
  padding: 12px 15px;
  font-size: 14px;
  transition: all 0.1s ease;
  cursor: pointer;
}

.base-select option:hover {
  background-color: var(--bg-extra-light);
  color: var(--primary-color);
}

.base-select option:checked {
  background-color: var(--primary-light);
  color: var(--primary-color);
}

.base-select option[value=""] {
  color: var(--text-placeholder);
  font-style: italic;
}

.error-message {
  display: block;
  color: var(--danger-color, #f56c6c);
  font-size: 12px;
  margin-top: 4px;
}
</style>

