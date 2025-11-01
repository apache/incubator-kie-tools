# 项目结构说明

## 📁 目录结构

```
database-snapshot-fetcher/
│
├── src/
│   ├── main/
│   │   ├── java/org/acme/database/model/          # Java数据模型
│   │   │   ├── HistoryResponse.java               # API响应数据模型
│   │   │   └── ArchivedValuesBatchRequest.java    # 批量查询请求模型
│   │   │
│   │   └── resources/                             # 资源文件
│   │       ├── specs/                             # OpenAPI规范
│   │       │   └── database-history-api.json      # 数据库历史API定义
│   │       │
│   │       ├── database-snapshot-workflow.sw.json # 定时工作流（自动执行）
│   │       ├── database-snapshot-manual.sw.json   # 手动工作流（POST触发）
│   │       └── application.properties             # 应用配置
│   │
│   └── test/
│       └── java/org/acme/
│           └── DatabaseSnapshotWorkflowTest.java  # 测试类
│
├── pom.xml                                        # Maven项目配置
├── package.json                                   # NPM项目配置
├── docker-compose.yml                             # Docker编排配置
│
├── start.sh                                       # Linux/Mac启动脚本
├── start.bat                                      # Windows启动脚本
├── test-api.sh                                    # API测试脚本
│
├── config.example.json                            # 配置示例文件
├── .gitignore                                     # Git忽略规则
│
├── README.md                                      # 完整文档
├── QUICKSTART.md                                  # 快速入门指南
└── PROJECT_STRUCTURE.md                           # 本文件
```

## 📄 核心文件说明

### 1. 工作流定义文件

#### `database-snapshot-workflow.sw.json` (定时工作流)
- **功能**: 使用Cron表达式定时自动执行
- **默认**: 每小时执行一次
- **调用API**: `History.GetArchivedValuesBatch`
- **流程**:
  1. FetchDatabaseSnapshot - 调用数据库API
  2. ValidateResponse - 验证响应状态
  3. ProcessSnapshot / HandleError - 处理结果或错误

#### `database-snapshot-manual.sw.json` (手动工作流)
- **功能**: 通过POST请求手动触发
- **端点**: `POST /database-snapshot-manual`
- **相同流程**: 与定时工作流相同的处理逻辑
- **区别**: 没有schedule配置，需要手动触发

### 2. OpenAPI规范

#### `specs/database-history-api.json`
定义了4个数据库API操作：
1. `getSingleValue` - 单个标签点单一时间点
2. `getValues` - 多个标签点单一时间点
3. `getArchivedValues` - 单个标签点时间范围
4. `getArchivedValuesBatch` - **多个标签点时间范围** ⭐

包含：
- 请求参数schema定义
- 响应数据schema定义
- 服务器URL配置（支持变量）

### 3. Java数据模型

#### `HistoryResponse.java`
API响应数据模型，对应：
```json
{
  "StatusCode": 200,
  "RequestID": "req-xxx",
  "Result": [...],
  "Error": ""
}
```

#### `ArchivedValuesBatchRequest.java`
批量查询请求模型，对应：
```json
{
  "tagNames": ["Tag001", "Tag002"],
  "count": 100,
  "startTime": "2024-01-01 00:00:00.000",
  "endTime": "2024-01-01 23:59:59.000"
}
```

### 4. 配置文件

#### `application.properties`
主要配置项：
- `database.api.server-ip` - 数据库API服务器IP
- `database.api.server-port` - 数据库API端口
- `quarkus.rest-client.*` - REST客户端配置
- `quarkus.http.*` - HTTP服务配置
- 日志、健康检查、容器镜像等配置

支持不同环境配置（dev, prod, container）

### 5. 构建配置

#### `pom.xml`
Maven依赖：
- `sonataflow-quarkus` - SonataFlow核心
- `quarkus-rest-client` - REST客户端
- `quarkus-resteasy-jackson` - JSON处理
- `quarkus-smallrye-health` - 健康检查
- `quarkus-smallrye-openapi` - OpenAPI/Swagger

#### `package.json`
NPM脚本：
- `pnpm build` - 构建项目
- `pnpm start` - 启动开发模式
- `pnpm test` - 运行测试

### 6. 辅助脚本

#### `start.sh` / `start.bat`
自动检查环境变量并启动Quarkus开发模式

#### `test-api.sh`
完整的API测试流程：
1. 健康检查
2. 触发工作流
3. 查询执行结果
4. 显示可用端点

### 7. Docker配置

#### `docker-compose.yml`
容器编排配置：
- 端口映射: 8080:8080
- 环境变量: DATABASE_API_SERVER_IP, DATABASE_API_SERVER_PORT
- 健康检查
- 网络配置

## 🔄 数据流程

```
                外部触发
                   ↓
    ┌─────────────────────────┐
    │  Serverless Workflow    │
    │  (定时或手动触发)         │
    └─────────────────────────┘
                   ↓
         调用 OpenAPI 函数
                   ↓
    ┌─────────────────────────┐
    │   Quarkus REST Client   │
    │   (根据OpenAPI规范)      │
    └─────────────────────────┘
                   ↓
         POST JSON 请求
                   ↓
    ┌─────────────────────────┐
    │   数据库历史API          │
    │   (外部服务)             │
    └─────────────────────────┘
                   ↓
         JSON 响应
                   ↓
    ┌─────────────────────────┐
    │   数据验证与处理         │
    │   (Workflow states)     │
    └─────────────────────────┘
                   ↓
         返回处理结果
```

## 🚀 运行流程

### 开发模式
```bash
mvn quarkus:dev
```
1. Maven加载依赖
2. Quarkus启动嵌入式服务器
3. 扫描并加载 .sw.json 工作流
4. 解析 OpenAPI 规范
5. 生成 REST 端点
6. 启动定时任务（如果有schedule配置）

### 生产模式
```bash
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```

### Docker模式
```bash
mvn clean package -Dcontainer
docker-compose up
```

## 📝 扩展指南

### 添加新的API端点

1. 在 `specs/database-history-api.json` 中添加新的path
2. 在workflow中添加新的function定义
3. 在state中调用新的function

### 添加DMN规则

1. 创建 `src/main/resources/your-rules.dmn`
2. 在workflow中添加规则调用state
3. 配置输入输出数据映射

### 添加数据持久化

1. 在 `pom.xml` 添加数据库依赖
2. 在 `application.properties` 配置数据源
3. 创建Entity类和Repository
4. 在workflow中添加数据保存逻辑

## 🔧 调试技巧

### 查看Workflow定义
```bash
curl http://localhost:8080/q/dev
```

### 查看OpenAPI规范
```bash
curl http://localhost:8080/q/openapi
```

### 查看生成的REST端点
访问: http://localhost:8080/q/swagger-ui

### 实时日志
开发模式下，所有日志会实时显示在控制台

## 📦 打包和部署

### JAR包
```bash
mvn clean package
# 输出: target/database-snapshot-fetcher-runner.jar
```

### Uber JAR
```bash
mvn clean package -Dquarkus.package.type=uber-jar
# 输出: target/database-snapshot-fetcher-runner.jar (单一文件)
```

### Native可执行文件
```bash
mvn clean package -Pnative
# 输出: target/database-snapshot-fetcher-runner (原生可执行文件)
```

### Docker镜像
```bash
mvn clean package -Dcontainer
# 输出: Docker镜像
```

## 🌟 最佳实践

1. **环境隔离**: 使用不同的profile配置（dev, test, prod）
2. **日志管理**: 适当设置日志级别
3. **错误处理**: workflow中包含错误处理state
4. **监控**: 使用health check和metrics
5. **安全**: 生产环境使用环境变量配置敏感信息
6. **版本控制**: 不提交敏感配置文件到Git

---

**HRL 诊断规则平台团队**
