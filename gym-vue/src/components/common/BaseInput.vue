<template>
  <div class="base-input">
    <div class="base-input__wrapper">
      <span v-if="$slots.prefix" class="base-input__prefix">
        <slot name="prefix"></slot>
      </span>
      <input
        :type="type"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :class="[
          'base-input__inner',
          {
            'base-input__inner--error': error,
            'base-input__inner--disabled': disabled,
          },
        ]"
        @input="handleInput"
        @blur="handleBlur"
        @focus="handleFocus"
      />
      <span v-if="showClear && modelValue" class="base-input__suffix" @click="handleClear">
        <span class="base-input__clear">✕</span>
      </span>
      <span v-else-if="$slots.suffix" class="base-input__suffix">
        <slot name="suffix"></slot>
      </span>
    </div>
    <div v-if="error" class="base-input__error">{{ error }}</div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  modelValue: string | number
  type?: 'text' | 'password' | 'email' | 'tel' | 'number'
  placeholder?: string
  disabled?: boolean
  error?: string
  showClear?: boolean
}

withDefaults(defineProps<Props>(), {
  type: 'text',
  disabled: false,
  showClear: false,
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
  blur: [event: FocusEvent]
  focus: [event: FocusEvent]
}>()

const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  emit('update:modelValue', target.value)
}

const handleBlur = (event: FocusEvent) => {
  emit('blur', event)
}

const handleFocus = (event: FocusEvent) => {
  emit('focus', event)
}

const handleClear = () => {
  emit('update:modelValue', '')
}
</script>

<style scoped>
.base-input {
  width: 100%;
}

.base-input__wrapper {
  position: relative;
  display: inline-flex;
  align-items: center;
  width: 100%;
}

.base-input__inner {
  flex: 1;
  width: 100%;
  padding: 10px 15px;
  border: 1px solid var(--border-base);
  border-radius: var(--border-radius-md);
  font-size: 14px;
  color: var(--text-primary);
  background-color: var(--bg-color);
  transition: border-color 0.3s;
  outline: none;
}

.base-input__inner:focus {
  border-color: var(--primary-color);
}

.base-input__inner--error {
  border-color: var(--danger-color);
}

.base-input__inner--disabled {
  background-color: #f5f7fa;
  cursor: not-allowed;
  color: var(--text-placeholder);
}

.base-input__prefix,
.base-input__suffix {
  display: flex;
  align-items: center;
  color: var(--text-secondary);
}

.base-input__prefix {
  padding-left: 12px;
}

.base-input__suffix {
  padding-right: 12px;
  cursor: pointer;
}

.base-input__clear {
  font-size: 16px;
  color: var(--text-placeholder);
  transition: color 0.3s;
}

.base-input__clear:hover {
  color: var(--text-secondary);
}

.base-input__error {
  margin-top: 4px;
  font-size: 12px;
  color: var(--danger-color);
  line-height: 1.5;
}
</style>
