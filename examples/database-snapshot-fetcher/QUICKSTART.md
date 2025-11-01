# 快速入门指南

这个指南将帮助你在5分钟内启动并运行数据库快照获取系统。

## 📋 准备工作

确保已安装：
- ✅ Java 17 或更高版本
- ✅ Maven 3.8 或更高版本
- ✅ 可访问的数据库历史API服务

## 🚀 快速启动（3步）

### 第1步：配置数据库API地址

**方式A - 修改配置文件** (推荐)

编辑 `src/main/resources/application.properties`：

```properties
database.api.server-ip=192.168.1.100  # 改成你的数据库API IP
database.api.server-port=5000          # 改成你的数据库API端口
```

**方式B - 设置环境变量**

Linux/Mac:
```bash
export DATABASE_API_SERVER_IP=192.168.1.100
export DATABASE_API_SERVER_PORT=5000
```

Windows (CMD):
```cmd
set DATABASE_API_SERVER_IP=192.168.1.100
set DATABASE_API_SERVER_PORT=5000
```

Windows (PowerShell):
```powershell
$env:DATABASE_API_SERVER_IP="192.168.1.100"
$env:DATABASE_API_SERVER_PORT="5000"
```

### 第2步：启动应用

**Linux/Mac:**
```bash
./start.sh
```

**Windows:**
```cmd
start.bat
```

**或者手动启动:**
```bash
mvn quarkus:dev
```

等待看到以下输出：
```
Listening on: http://localhost:8080
```

### 第3步：测试

打开浏览器访问：
- **Swagger UI**: http://localhost:8080/q/swagger-ui
- **健康检查**: http://localhost:8080/q/health

或者运行测试脚本：
```bash
./test-api.sh
```

## 📡 手动触发数据获取

```bash
curl -X POST http://localhost:8080/database-snapshot-manual \
  -H "Content-Type: application/json" \
  -d '{
    "tagNames": ["Tag001", "Tag002", "Tag003"],
    "count": 100,
    "startTime": "2024-01-01 00:00:00.000",
    "endTime": "2024-01-01 23:59:59.000"
  }'
```

**成功响应示例:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "workflowdata": {
    "status": "success",
    "data": [...],
    "requestId": "req-12345678"
  }
}
```

## ⏰ 配置定时任务

编辑 `src/main/resources/database-snapshot-workflow.sw.json`：

```json
{
  "start": {
    "schedule": {
      "cron": "0 0 * * * ?"  // 修改这里
    }
  }
}
```

常用Cron表达式：
- `0 0 * * * ?` - 每小时
- `0 */15 * * * ?` - 每15分钟
- `0 0 0 * * ?` - 每天午夜
- `0 */5 * * * ?` - 每5分钟

## 🐳 Docker部署（可选）

### 方式1: 构建并运行

```bash
# 1. 构建Docker镜像
mvn clean package -Dcontainer

# 2. 修改docker-compose.yml中的数据库API地址

# 3. 启动容器
docker-compose up -d
```

### 方式2: 手动Docker命令

```bash
docker run -d \
  --name database-snapshot-fetcher \
  -p 8080:8080 \
  -e DATABASE_API_SERVER_IP=192.168.1.100 \
  -e DATABASE_API_SERVER_PORT=5000 \
  dev.local/$USER/database-snapshot-fetcher:1.0-SNAPSHOT
```

## 📊 监控和日志

查看日志：
```bash
# 开发模式日志会直接显示在控制台

# Docker容器日志
docker logs -f database-snapshot-fetcher
```

健康检查：
```bash
curl http://localhost:8080/q/health
```

## 🔧 常见问题

### Q: 启动失败，提示端口被占用

**A:** 修改端口
```properties
quarkus.http.port=8081  # 在application.properties中修改
```

### Q: 无法连接到数据库API

**A:** 检查清单
1. 数据库API服务是否运行
2. IP地址和端口是否正确
3. 网络是否可达（ping测试）
4. 防火墙是否允许访问

测试连接：
```bash
curl -X POST http://192.168.1.100:5000/DataAdapter/History.GetSingleValue \
  -H "Content-Type: application/json" \
  -d '{"tagName":"Tag001","dateTime":"2024-01-01 12:00:00.000","historyMode":0}'
```

### Q: 定时任务没有执行

**A:** 检查
1. 查看日志确认Cron表达式
2. 确认workflow文件正确加载
3. 检查是否有异常阻止启动

### Q: 想要添加自己的数据处理逻辑

**A:** 在workflow中添加新的state：

```json
{
  "name": "ProcessData",
  "type": "operation",
  "actions": [{
    "functionRef": {
      "refName": "yourCustomFunction"
    }
  }]
}
```

## 📚 下一步

- 阅读完整文档: [README.md](README.md)
- 添加DMN规则分析
- 配置数据持久化
- 设置监控告警

## 🆘 需要帮助？

查看详细文档：[README.md](README.md)

---

**HRL 诊断规则平台团队**
