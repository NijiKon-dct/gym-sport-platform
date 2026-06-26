/**
 * 错误处理工具
 */

export interface ErrorInfo {
  message: string
  code?: string | number
  stack?: string
}

/**
 * 错误处理函数
 */
export function handleError(error: Error | ErrorInfo | unknown): void {
  let errorInfo: ErrorInfo

  if (error instanceof Error) {
    errorInfo = {
      message: error.message,
      stack: error.stack,
    }
  } else if (typeof error === 'object' && error !== null) {
    errorInfo = error as ErrorInfo
  } else {
    errorInfo = {
      message: String(error),
    }
  }

  console.error('Error:', errorInfo)

  // 这里可以集成错误监控服务，如 Sentry
  // Sentry.captureException(error)
}

/**
 * 显示错误消息
 */
export function showError(message: string): void {
  // 这里可以使用 Element Plus 的 Message 组件
  // ElMessage.error(message)
  alert(message)
}

/**
 * 显示成功消息
 */
export function showSuccess(message: string): void {
  // 这里可以使用 Element Plus 的 Message 组件
  // ElMessage.success(message)
  alert(message)
}

