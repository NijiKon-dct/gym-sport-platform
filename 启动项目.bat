@echo off
chcp 65001 >nul
echo ========================================
echo   健身场馆预约管理系统 - 自动启动脚本
echo ========================================
echo.

:: 获取脚本所在目录
set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%"

:: 检查后端目录是否存在
if not exist "gymbackend" (
    echo [错误] 后端目录不存在: gymbackend
    pause
    exit /b 1
)

:: 检查后端mvnw.cmd是否存在
if not exist "gymbackend\mvnw.cmd" (
    echo [错误] 找不到 mvnw.cmd 文件，请确认后端项目结构正确
    pause
    exit /b 1
)

:: 检查前端目录是否存在
if not exist "gym-vue" (
    echo [错误] 前端目录不存在: gym-vue
    pause
    exit /b 1
)

echo [1/3] 正在启动后端服务...
start "后端服务 - Gym Backend" cmd /k "cd /d %SCRIPT_DIR%gymbackend && echo 后端服务正在启动，请稍候... && .\mvnw.cmd spring-boot:run"

echo [2/3] 等待后端服务初始化（10秒）...
timeout /t 10 /nobreak >nul

echo [3/3] 正在启动前端服务...
start "前端服务 - Gym Vue" cmd /k "cd /d %SCRIPT_DIR%gym-vue && echo 前端服务正在启动，请稍候... && npm run dev"

echo.
echo ========================================
echo   启动完成！
echo ========================================
echo.
echo 后端服务: http://localhost:8080
echo 前端服务: http://localhost:5173
echo.
echo 提示：
echo - 两个服务窗口已打开，请不要关闭
echo - 按 Ctrl+C 可以停止对应的服务
echo - 关闭窗口即可停止服务
echo.
echo 按任意键关闭此窗口（服务将继续运行）...
pause >nul

