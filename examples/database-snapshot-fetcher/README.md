# 数据库快照定时获取系统

这是一个基于Apache KIE SonataFlow的数据库历史数据定时获取与分析系统。系统能够主动从数据库API获取标签点的历史快照数据，而不是被动接收POST请求。

## 功能特性

- ✅ **定时数据拉取**: 使用Cron表达式定时从数据库API获取数据（默认每小时）
- ✅ **手动触发**: 支持通过REST API手动触发数据获取
- ✅ **批量标签点查询**: 一次性获取多个标签点的历史数据
- ✅ **错误处理**: 完善的错误处理和日志记录
- ✅ **OpenAPI集成**: 基于OpenAPI规范定义外部API调用
- ✅ **可配置**: 通过配置文件或环境变量配置数据库API地址

## 项目结构

```
database-snapshot-fetcher/
├── src/
│   └── main/
│       ├── java/org/acme/database/model/
│       │   ├── HistoryResponse.java              # 响应数据模型
│       │   └── ArchivedValuesBatchRequest.java   # 请求数据模型
│       └── resources/
│           ├── specs/
│           │   └── database-history-api.json     # OpenAPI规范定义
│           ├── database-snapshot-workflow.sw.json  # 定时工作流
│           ├── database-snapshot-manual.sw.json    # 手动工作流
│           └── application.properties            # 应用配置
├── pom.xml                                       # Maven配置
├── package.json                                  # NPM配置
└── README.md                                     # 本文档
```

## 快速开始

### 前置要求

- Java 17+
- Maven 3.8+
- Node.js 18+ 和 pnpm
- 可访问的数据库历史API服务

### 1. 配置数据库API地址

编辑 `src/main/resources/application.properties`：

```properties
# 修改为你的数据库API地址
database.api.server-ip=192.168.1.100
database.api.server-port=5000
```

或者通过环境变量设置：

```bash
export DATABASE_API_SERVER_IP=192.168.1.100
export DATABASE_API_SERVER_PORT=5000
```

### 2. 构建项目

```bash
cd examples/database-snapshot-fetcher

# 安装依赖并构建
pnpm build

# 或者只用Maven构建
mvn clean install
```

### 3. 运行开发模式

```bash
# 使用pnpm启动
pnpm start

# 或者使用Maven启动
mvn quarkus:dev
```

应用将在 `http://localhost:8080` 启动。

### 4. 访问Swagger UI

浏览器打开：`http://localhost:8080/q/swagger-ui`

你将看到自动生成的REST API文档，包括：
- `/database-snapshot-workflow` - 定时工作流（自动运行）
- `/database-snapshot-manual` - 手动工作流（需要POST触发）

## 使用方法

### 方式1: 定时自动获取（推荐）

定时工作流会根据Cron表达式自动运行。默认配置是**每小时**执行一次。

修改定时规则，编辑 `src/main/resources/database-snapshot-workflow.sw.json`：

```json
{
  "start": {
    "stateName": "FetchDatabaseSnapshot",
    "schedule": {
      "cron": "0 0 * * * ?"  // 每小时
      // "cron": "0 */15 * * * ?"  // 每15分钟
      // "cron": "0 0 0 * * ?"  // 每天午夜
    }
  }
}
```

Cron表达式格式：`秒 分 时 日 月 星期`

### 方式2: 手动触发获取

通过POST请求手动触发数据获取：

```bash
curl -X POST http://localhost:8080/database-snapshot-manual \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "tagNames": ["Tag001", "Tag002", "Tag003"],
    "count": 100,
    "startTime": "2024-01-01 00:00:00.000",
    "endTime": "2024-01-01 23:59:59.000"
  }'
```

**响应示例（成功）**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "workflowdata": {
    "status": "success",
    "data": [...],
    "requestId": "req-12345678",
    "fetchTime": "2024-01-01T12:00:00Z"
  }
}
```

**响应示例（失败）**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "workflowdata": {
    "status": "error",
    "error": "连接超时",
    "statusCode": 500,
    "fetchTime": "2024-01-01T12:00:00Z"
  }
}
```

### 查询工作流实例状态

```bash
# 获取所有工作流实例
curl http://localhost:8080/database-snapshot-manual

# 获取特定实例详情
curl http://localhost:8080/database-snapshot-manual/{instanceId}
```

## 数据库API规范

系统支持以下数据库API操作（在OpenAPI规范中定义）：

### 1. 获取单个标签点单一历史断面数据

**Endpoint**: `POST /DataAdapter/History.GetSingleValue`

```json
{
  "tagName": "Tag001",
  "dateTime": "2024-01-01 12:00:00.000",
  "historyMode": 0
}
```

### 2. 获取多个标签点在某一时间的历史断面数据

**Endpoint**: `POST /DataAdapter/History.GetValues`

```json
{
  "tagNames": ["Tag001", "Tag002"],
  "dateTime": "2024-01-01 12:00:00.000",
  "historyMode": 0
}
```

### 3. 获取单个标签点一段时间内的储存数据

**Endpoint**: `POST /DataAdapter/History.GetArchivedValues`

```json
{
  "tagName": "Tag001",
  "count": 100,
  "startTime": "2024-01-01 00:00:00.000",
  "endTime": "2024-01-01 23:59:59.000"
}
```

### 4. 获取多个标签点一段时间内的储存数据 ⭐

**Endpoint**: `POST /DataAdapter/History.GetArchivedValuesBatch`

```json
{
  "tagNames": ["Tag001", "Tag002", "Tag003"],
  "count": 100,
  "startTime": "2024-01-01 00:00:00.000",
  "endTime": "2024-01-01 23:59:59.000"
}
```

**响应格式**:
```json
{
  "StatusCode": 200,
  "RequestID": "req-12345678",
  "Result": [...],
  "Error": ""
}
```

## 配置说明

### application.properties主要配置项

```properties
# 数据库API服务地址
database.api.server-ip=localhost
database.api.server-port=5000

# HTTP端口
quarkus.http.port=8080

# REST客户端超时设置
quarkus.rest-client."database-history-api".connect-timeout=10000
quarkus.rest-client."database-history-api".read-timeout=30000

# 日志级别
quarkus.log.level=INFO
quarkus.log.category."org.acme".level=DEBUG
```

### 环境变量配置（生产环境推荐）

```bash
# 数据库API地址
export DATABASE_API_SERVER_IP=192.168.1.100
export DATABASE_API_SERVER_PORT=5000

# HTTP端口
export QUARKUS_HTTP_PORT=8080
```

## 部署

### 方式1: JAR部署

```bash
# 构建JAR包
mvn clean package

# 运行
java -jar target/quarkus-app/quarkus-run.jar
```

### 方式2: Docker容器部署

```bash
# 构建容器镜像
mvn clean package -Dcontainer

# 运行容器
docker run -i --rm -p 8080:8080 \
  -e DATABASE_API_SERVER_IP=192.168.1.100 \
  -e DATABASE_API_SERVER_PORT=5000 \
  dev.local/${USER}/database-snapshot-fetcher:1.0-SNAPSHOT
```

### 方式3: Native编译（更快启动，更少内存）

```bash
# 构建native可执行文件
mvn clean package -Pnative

# 运行
./target/database-snapshot-fetcher-runner
```

## 监控和管理

### 健康检查

```bash
# 健康检查端点
curl http://localhost:8080/q/health

# 健康检查UI
open http://localhost:8080/q/health-ui
```

### 指标监控

```bash
# Prometheus指标
curl http://localhost:8080/q/metrics
```

### 日志查看

应用日志会输出到控制台，包括：
- 工作流执行状态
- API调用详情
- 数据获取结果
- 错误信息

## 扩展功能

### 添加DMN规则分析

你可以在工作流中添加DMN规则来分析获取的数据：

1. 创建DMN规则文件：`src/main/resources/diagnostic-rules.dmn`
2. 在workflow中添加规则执行状态：

```json
{
  "name": "ExecuteDiagnosticRules",
  "type": "operation",
  "actions": [{
    "functionRef": {
      "refName": "executeDMNRules",
      "arguments": {
        "snapshotData": ".snapshotData.Result"
      }
    }
  }]
}
```

### 数据持久化

添加数据库依赖（PostgreSQL示例）：

```xml
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-jdbc-postgresql</artifactId>
</dependency>
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-hibernate-orm</artifactId>
</dependency>
```

配置：
```properties
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=postgres
quarkus.datasource.password=postgres
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/snapshot_db
```

## 故障排查

### 问题1: 无法连接到数据库API

**检查清单**:
- ✓ 数据库API服务是否运行？
- ✓ IP地址和端口配置是否正确？
- ✓ 网络是否可达？（ping测试）
- ✓ 防火墙是否允许访问？

**解决方法**:
```bash
# 测试API连接
curl -X POST http://192.168.1.100:5000/DataAdapter/History.GetSingleValue \
  -H "Content-Type: application/json" \
  -d '{"tagName":"Tag001","dateTime":"2024-01-01 12:00:00.000","historyMode":0}'
```

### 问题2: 定时任务未执行

**检查**:
- 查看日志确认Cron表达式是否正确
- 确认workflow文件已正确加载
- 检查是否有异常阻止了workflow启动

### 问题3: 数据格式错误

**检查**:
- 时间格式必须是: `YYYY-MM-DD HH:mm:ss.fff`
- tagNames必须是字符串数组
- count必须是整数

## API端点总览

| 端点 | 方法 | 描述 |
|------|------|------|
| `/database-snapshot-workflow` | POST | 定时工作流（自动触发） |
| `/database-snapshot-manual` | POST | 手动触发数据获取 |
| `/database-snapshot-manual/{id}` | GET | 查询工作流实例状态 |
| `/q/swagger-ui` | GET | Swagger API文档 |
| `/q/health` | GET | 健康检查 |
| `/q/metrics` | GET | Prometheus指标 |

## 技术栈

- **Apache KIE SonataFlow**: Serverless Workflow引擎
- **Quarkus**: 云原生Java框架
- **RESTEasy**: REST客户端和服务端
- **Jackson**: JSON序列化/反序列化
- **OpenAPI 3.0**: API规范定义

## 参考资料

- [SonataFlow文档](https://sonataflow.org/)
- [Serverless Workflow规范](https://serverlessworkflow.io/)
- [Quarkus文档](https://quarkus.io/)
- [OpenAPI规范](https://swagger.io/specification/)

## 许可证

Apache License 2.0

## 作者

HRL 诊断规则平台团队
