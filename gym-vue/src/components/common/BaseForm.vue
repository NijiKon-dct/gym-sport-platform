<template>
  <form class="base-form" @submit.prevent="handleSubmit">
    <slot></slot>
  </form>
</template>

<script setup lang="ts">
const emit = defineEmits<{
  submit: []
}>()

const handleSubmit = () => {
  emit('submit')
}
</script>

<style scoped>
.base-form {
  width: 100%;
}
</style>

