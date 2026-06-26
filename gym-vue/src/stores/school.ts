import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export interface SchoolOption {
  id: number
  name: string
  enabled: boolean
  sortOrder: number
}

export const useSchoolStore = defineStore('school', () => {
  const schools = ref<string[]>([])
  const schoolOptions = ref<SchoolOption[]>([])
  const loading = ref(false)

  const fetchSchoolOptions = async () => {
    loading.value = true
    try {
      const res = await request.get<SchoolOption[]>('/schools')
      schoolOptions.value = res || []
      schools.value = schoolOptions.value.filter((s) => s.enabled).map((s) => s.name)
    } finally {
      loading.value = false
    }
  }

  const fetchEnabledNames = async () => {
    loading.value = true
    try {
      const res = await request.get<string[]>('/schools/options')
      schools.value = res || []
    } finally {
      loading.value = false
    }
  }

  const addSchool = async (name: string) => {
    const trimmed = name.trim()
    if (!trimmed) return false
    if (schools.value.includes(trimmed)) return false
    const created = await request.post<SchoolOption>('/schools', { name: trimmed })
    if (created) {
      schoolOptions.value.push(created)
      if (created.enabled) {
        schools.value.push(created.name)
      }
    }
    return true
  }

  const removeSchool = async (name: string) => {
    const target = schoolOptions.value.find((item) => item.name === name)
    if (!target) return
    await request.delete(`/schools/${target.id}`)
    schoolOptions.value = schoolOptions.value.filter((item) => item.id !== target.id)
    schools.value = schools.value.filter((item) => item !== name)
  }

  const resetToDefault = async () => {
    // 目前后端没有“恢复默认”接口，这里简单地重新拉取启用列表
    await fetchEnabledNames()
  }

  return {
    schools,
    schoolOptions,
    loading,
    fetchSchoolOptions,
    fetchEnabledNames,
    addSchool,
    removeSchool,
    resetToDefault,
  }
})
