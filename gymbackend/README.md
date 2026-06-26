# Gym Backend 服务

Spring Boot 3 + MySQL 的后端，负责为前端 `gym-vue` 提供真实数据接口。

## 快速开始

1. 创建数据库（默认名称 `gymdb`，可自定义）：
   ```sql
   CREATE DATABASE gymdb DEFAULT CHARACTER SET utf8mb4;
   ```
2. 配置环境变量或在 `application.properties` 中覆盖以下参数：
   - `DB_URL`（默认：`jdbc:mysql://localhost:3306/gymdb?...`）
   - `DB_USERNAME`（默认：`root`）
   - `DB_PASSWORD`（默认：`123456`）
3. 启动服务：
   ```bash
   cd gymbackend/gymbackend
   ./mvnw spring-boot:run
   ```

首次启动会自动建表并注入演示数据（管理员账号 `1001/123456`）。

## 主要模块

| 模块     | 描述                           |
|----------|--------------------------------|
| 场馆     | CRUD / 预约冲突校验 / 统计接口 |
| 用户     | 登录注册 / 资料维护 / 搜索     |
| 社区     | 动态、评论、点赞               |
| 好友     | 请求审批 / 列表 / 删除         |
| 聊天     | 会话列表 / 消息记录 / 既读     |

所有接口统一前缀 `/api`，响应结构为：

```json
{
  "success": true,
  "message": "OK",
  "data": { ... }
}
```


















