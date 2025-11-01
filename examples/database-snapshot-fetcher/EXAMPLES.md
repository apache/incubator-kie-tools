# 使用示例

这个文档包含了数据库快照获取系统的各种使用示例。

## 📋 目录

1. [基础示例](#基础示例)
2. [定时任务示例](#定时任务示例)
3. [高级查询示例](#高级查询示例)
4. [错误处理示例](#错误处理示例)
5. [集成示例](#集成示例)

---

## 基础示例

### 示例1: 手动触发获取最近1小时的数据

```bash
curl -X POST http://localhost:8080/database-snapshot-manual \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "tagNames": ["温度传感器01", "压力传感器01", "流量计01"],
    "count": 3600,
    "startTime": "2024-11-01 11:00:00.000",
    "endTime": "2024-11-01 12:00:00.000"
  }'
```

**响应:**
```json
{
  "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "workflowdata": {
    "status": "success",
    "data": [
      {
        "tagName": "温度传感器01",
        "values": [...]
      }
    ],
    "requestId": "req-20241101-001",
    "fetchTime": "2024-11-01T12:00:05Z"
  }
}
```

### 示例2: 获取多个标签点过去24小时的数据

```bash
# 使用变量简化命令
END_TIME=$(date "+%Y-%m-%d %H:00:00.000")
START_TIME=$(date -d "24 hours ago" "+%Y-%m-%d %H:00:00.000")

curl -X POST http://localhost:8080/database-snapshot-manual \
  -H "Content-Type: application/json" \
  -d "{
    \"tagNames\": [\"Tag001\", \"Tag002\", \"Tag003\", \"Tag004\", \"Tag005\"],
    \"count\": 1440,
    \"startTime\": \"$START_TIME\",
    \"endTime\": \"$END_TIME\"
  }"
```

### 示例3: 查询工作流执行状态

```bash
# 保存工作流ID
WORKFLOW_ID="f47ac10b-58cc-4372-a567-0e02b2c3d479"

# 查询状态
curl http://localhost:8080/database-snapshot-manual/$WORKFLOW_ID | jq '.'
```

**响应:**
```json
{
  "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "workflowdata": {
    "status": "success",
    "data": [...],
    "requestId": "req-20241101-001",
    "fetchTime": "2024-11-01T12:00:05Z"
  }
}
```

---

## 定时任务示例

### 示例4: 每15分钟获取一次数据

编辑 `src/main/resources/database-snapshot-workflow.sw.json`:

```json
{
  "id": "database-snapshot-workflow",
  "start": {
    "stateName": "FetchDatabaseSnapshot",
    "schedule": {
      "cron": "0 */15 * * * ?"
    }
  },
  "functions": [...],
  "states": [...]
}
```

### 示例5: 每天凌晨2点获取前一天的数据

```json
{
  "start": {
    "schedule": {
      "cron": "0 0 2 * * ?"
    }
  }
}
```

在workflow的初始state中动态计算时间：

```json
{
  "name": "PrepareTimeRange",
  "type": "inject",
  "data": {
    "endTime": "${ now() | strftime(\"%Y-%m-%d 00:00:00.000\") }",
    "startTime": "${ (now() - 86400) | strftime(\"%Y-%m-%d 00:00:00.000\") }"
  },
  "transition": "FetchDatabaseSnapshot"
}
```

### 示例6: 工作日每小时执行

```json
{
  "schedule": {
    "cron": "0 0 * * * MON-FRI"
  }
}
```

---

## 高级查询示例

### 示例7: 批量查询多组标签点

```bash
# 第一组：温度传感器
curl -X POST http://localhost:8080/database-snapshot-manual \
  -H "Content-Type: application/json" \
  -d '{
    "tagNames": ["温度01", "温度02", "温度03", "温度04", "温度05"],
    "count": 100,
    "startTime": "2024-11-01 00:00:00.000",
    "endTime": "2024-11-01 12:00:00.000"
  }'

# 第二组：压力传感器
curl -X POST http://localhost:8080/database-snapshot-manual \
  -H "Content-Type: application/json" \
  -d '{
    "tagNames": ["压力01", "压力02", "压力03", "压力04", "压力05"],
    "count": 100,
    "startTime": "2024-11-01 00:00:00.000",
    "endTime": "2024-11-01 12:00:00.000"
  }'
```

### 示例8: Python脚本自动化批量查询

创建文件 `batch_query.py`:

```python
#!/usr/bin/env python3
import requests
import json
from datetime import datetime, timedelta

# 配置
BASE_URL = "http://localhost:8080"
ENDPOINT = f"{BASE_URL}/database-snapshot-manual"

# 时间范围
end_time = datetime.now()
start_time = end_time - timedelta(hours=1)

# 标签点分组
tag_groups = {
    "温度传感器": ["温度01", "温度02", "温度03"],
    "压力传感器": ["压力01", "压力02", "压力03"],
    "流量计": ["流量01", "流量02", "流量03"]
}

# 批量查询
results = {}
for group_name, tags in tag_groups.items():
    payload = {
        "tagNames": tags,
        "count": 100,
        "startTime": start_time.strftime("%Y-%m-%d %H:%M:%S.000"),
        "endTime": end_time.strftime("%Y-%m-%d %H:%M:%S.000")
    }

    response = requests.post(ENDPOINT, json=payload)
    if response.status_code == 201:
        results[group_name] = response.json()
        print(f"✓ {group_name}: 工作流ID = {results[group_name]['id']}")
    else:
        print(f"✗ {group_name}: 失败 - {response.status_code}")

# 保存结果
with open("query_results.json", "w") as f:
    json.dump(results, f, indent=2, ensure_ascii=False)

print(f"\n总计查询 {len(results)} 组标签点")
```

运行：
```bash
chmod +x batch_query.py
./batch_query.py
```

### 示例9: 使用jq处理响应数据

```bash
# 提取所有成功的数据
curl -s http://localhost:8080/database-snapshot-manual/$WORKFLOW_ID | \
  jq '.workflowdata | select(.status == "success") | .data'

# 统计数据点数量
curl -s http://localhost:8080/database-snapshot-manual/$WORKFLOW_ID | \
  jq '.workflowdata.data | length'

# 提取RequestID
curl -s http://localhost:8080/database-snapshot-manual/$WORKFLOW_ID | \
  jq -r '.workflowdata.requestId'
```

---

## 错误处理示例

### 示例10: 处理API超时

修改 `application.properties`:

```properties
# 增加超时时间到60秒
quarkus.rest-client."database-history-api".read-timeout=60000
quarkus.rest-client."database-history-api".connect-timeout=15000
```

### 示例11: 重试机制

在workflow中添加重试逻辑（修改 `database-snapshot-manual.sw.json`）:

```json
{
  "name": "FetchDatabaseSnapshot",
  "type": "operation",
  "actions": [
    {
      "name": "获取数据库历史快照",
      "functionRef": {
        "refName": "getArchivedValuesBatch",
        "arguments": {...}
      }
    }
  ],
  "onErrors": [
    {
      "errorRef": "TimeoutError",
      "transition": "RetryFetch"
    }
  ]
},
{
  "name": "RetryFetch",
  "type": "sleep",
  "duration": "PT5S",
  "transition": "FetchDatabaseSnapshot"
}
```

### 示例12: 错误通知

添加错误通知state:

```json
{
  "name": "HandleError",
  "type": "operation",
  "actions": [
    {
      "name": "发送错误通知",
      "functionRef": {
        "refName": "sendNotification",
        "arguments": {
          "level": "error",
          "message": "\"数据获取失败: \" + .snapshotData.Error",
          "timestamp": "${ now() }"
        }
      }
    }
  ],
  "end": true
}
```

---

## 集成示例

### 示例13: 与Prometheus集成监控

访问指标端点：
```bash
curl http://localhost:8080/q/metrics
```

添加到 `prometheus.yml`:
```yaml
scrape_configs:
  - job_name: 'database-snapshot-fetcher'
    metrics_path: '/q/metrics'
    static_configs:
      - targets: ['localhost:8080']
```

### 示例14: 通过Webhook触发

创建一个简单的webhook服务：

```python
#!/usr/bin/env python3
from flask import Flask, request
import requests

app = Flask(__name__)
WORKFLOW_URL = "http://localhost:8080/database-snapshot-manual"

@app.route('/webhook/trigger', methods=['POST'])
def trigger_workflow():
    # 从webhook获取参数
    data = request.json

    # 构造workflow请求
    payload = {
        "tagNames": data.get("tags", ["Tag001"]),
        "count": data.get("count", 100),
        "startTime": data.get("startTime"),
        "endTime": data.get("endTime")
    }

    # 触发workflow
    response = requests.post(WORKFLOW_URL, json=payload)
    return response.json()

if __name__ == '__main__':
    app.run(port=5001)
```

触发：
```bash
curl -X POST http://localhost:5001/webhook/trigger \
  -H "Content-Type: application/json" \
  -d '{
    "tags": ["Tag001", "Tag002"],
    "count": 100,
    "startTime": "2024-11-01 00:00:00.000",
    "endTime": "2024-11-01 12:00:00.000"
  }'
```

### 示例15: 数据导出到CSV

创建 `export_to_csv.sh`:

```bash
#!/bin/bash

WORKFLOW_ID=$1
OUTPUT_FILE="snapshot_data_$(date +%Y%m%d_%H%M%S).csv"

# 获取数据
DATA=$(curl -s http://localhost:8080/database-snapshot-manual/$WORKFLOW_ID)

# 使用jq转换为CSV
echo "$DATA" | jq -r '
  .workflowdata.data[] |
  [.tagName, .timestamp, .value, .quality] |
  @csv
' > $OUTPUT_FILE

echo "数据已导出到: $OUTPUT_FILE"
```

使用：
```bash
./export_to_csv.sh f47ac10b-58cc-4372-a567-0e02b2c3d479
```

### 示例16: 与数据库集成保存结果

添加PostgreSQL依赖到 `pom.xml`:

```xml
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-jdbc-postgresql</artifactId>
</dependency>
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-hibernate-orm-panache</artifactId>
</dependency>
```

配置数据库 `application.properties`:

```properties
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=postgres
quarkus.datasource.password=postgres
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/snapshot_db

quarkus.hibernate-orm.database.generation=update
```

创建Entity:

```java
@Entity
@Table(name = "snapshot_records")
public class SnapshotRecord extends PanacheEntity {

    @Column(name = "workflow_id")
    public String workflowId;

    @Column(name = "request_id")
    public String requestId;

    @Column(name = "status")
    public String status;

    @Column(name = "fetch_time")
    public Instant fetchTime;

    @Column(name = "data", columnDefinition = "jsonb")
    public String data;
}
```

---

## 🔧 Shell脚本工具集

### 完整的管理脚本

创建 `manage.sh`:

```bash
#!/bin/bash

function show_menu() {
    echo "================================"
    echo "数据库快照获取系统 - 管理工具"
    echo "================================"
    echo "1. 启动服务"
    echo "2. 停止服务"
    echo "3. 查看日志"
    echo "4. 触发手动获取"
    echo "5. 查看最近的工作流"
    echo "6. 健康检查"
    echo "7. 查看Swagger UI"
    echo "0. 退出"
    echo "================================"
}

function trigger_fetch() {
    echo "请输入标签点（用逗号分隔）："
    read tags

    echo "请输入开始时间（格式：YYYY-MM-DD HH:mm:ss）："
    read start_time

    echo "请输入结束时间（格式：YYYY-MM-DD HH:mm:ss）："
    read end_time

    IFS=',' read -ra TAG_ARRAY <<< "$tags"
    TAG_JSON=$(printf ',"%s"' "${TAG_ARRAY[@]}")
    TAG_JSON="[${TAG_JSON:1}]"

    curl -X POST http://localhost:8080/database-snapshot-manual \
      -H "Content-Type: application/json" \
      -d "{
        \"tagNames\": $TAG_JSON,
        \"count\": 100,
        \"startTime\": \"${start_time}.000\",
        \"endTime\": \"${end_time}.000\"
      }"
}

while true; do
    show_menu
    read -p "请选择操作: " choice

    case $choice in
        1) ./start.sh ;;
        2) pkill -f "quarkus:dev" ;;
        3) tail -f logs/application.log ;;
        4) trigger_fetch ;;
        5) curl -s http://localhost:8080/database-snapshot-manual | jq '.' ;;
        6) curl -s http://localhost:8080/q/health | jq '.' ;;
        7) open http://localhost:8080/q/swagger-ui ;;
        0) exit 0 ;;
        *) echo "无效选择" ;;
    esac

    echo ""
    read -p "按Enter继续..."
done
```

---

## 📊 监控和分析示例

### 示例17: 实时监控脚本

```bash
#!/bin/bash

echo "数据库快照获取系统 - 实时监控"
echo "======================================"

while true; do
    clear
    echo "时间: $(date)"
    echo "======================================"

    # 健康状态
    echo "健康状态:"
    curl -s http://localhost:8080/q/health | jq -r '.status'
    echo ""

    # 最近的工作流
    echo "最近的工作流执行:"
    curl -s http://localhost:8080/database-snapshot-manual | \
      jq -r '.[] | "\(.id): \(.workflowdata.status)"' | head -5

    echo "======================================"
    sleep 5
done
```

---

**更多示例和最佳实践，请参考 [README.md](README.md)**
