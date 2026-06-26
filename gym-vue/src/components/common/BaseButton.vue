<template>
  <button
    :type="htmlType"
    :class="[
      'base-button',
      `base-button--${type}`,
      `base-button--${size}`,
      {
        'base-button--loading': loading,
        'base-button--disabled': disabled,
      }
    ]"
    :disabled="disabled || loading"
    @click="handleClick"
  >
    <span v-if="loading" class="base-button__loading-icon">⏳</span>
    <slot></slot>
  </button>
</template>

<script setup lang="ts">
interface Props {
  type?: 'primary' | 'secondary' | 'success' | 'warning' | 'danger'
  htmlType?: 'button' | 'submit' | 'reset'
  size?: 'small' | 'medium' | 'large'
  loading?: boolean
  disabled?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'primary',
  htmlType: 'button',
  size: 'medium',
  loading: false,
  disabled: false,
})

const emit = defineEmits<{
  click: [event: MouseEvent]
}>()

const handleClick = (event: MouseEvent) => {
  if (!props.disabled && !props.loading) {
    emit('click', event)
  }
}
</script>

<style scoped>
.base-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 20px;
  border: none;
  border-radius: var(--border-radius-md);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  outline: none;
}

.base-button--small {
  padding: 6px 12px;
  font-size: 12px;
}

.base-button--medium {
  padding: 10px 20px;
  font-size: 14px;
}

.base-button--large {
  padding: 14px 28px;
  font-size: 16px;
}

.base-button--primary {
  background-color: var(--primary-color);
  color: #fff;
}

.base-button--primary:hover:not(.base-button--disabled) {
  background-color: #66b1ff;
}

.base-button--secondary {
  background-color: #f4f4f5;
  color: var(--text-primary);
}

.base-button--secondary:hover:not(.base-button--disabled) {
  background-color: #e9e9eb;
}

.base-button--success {
  background-color: var(--success-color);
  color: #fff;
}

.base-button--success:hover:not(.base-button--disabled) {
  background-color: #85ce61;
}

.base-button--warning {
  background-color: var(--warning-color);
  color: #fff;
}

.base-button--warning:hover:not(.base-button--disabled) {
  background-color: #ebb563;
}

.base-button--danger {
  background-color: var(--danger-color);
  color: #fff;
}

.base-button--danger:hover:not(.base-button--disabled) {
  background-color: #f78989;
}

.base-button--disabled,
.base-button--disabled:hover {
  opacity: 0.6;
  cursor: not-allowed;
}

.base-button--loading {
  cursor: not-allowed;
}

.base-button__loading-icon {
  margin-right: 6px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>

