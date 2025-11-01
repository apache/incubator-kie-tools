#!/bin/bash

#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

BASE_URL="http://localhost:8080"

echo "=========================================="
echo "数据库快照获取系统 - API测试脚本"
echo "=========================================="
echo ""

# 检查服务是否运行
echo "1. 检查服务健康状态..."
curl -s "$BASE_URL/q/health" | grep -q "UP"
if [ $? -eq 0 ]; then
    echo "   ✓ 服务运行正常"
else
    echo "   ❌ 服务未运行，请先启动应用 (./start.sh)"
    exit 1
fi
echo ""

# 测试手动触发工作流
echo "2. 测试手动触发数据获取..."
RESPONSE=$(curl -s -X POST "$BASE_URL/database-snapshot-manual" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "tagNames": ["Tag001", "Tag002", "Tag003"],
    "count": 100,
    "startTime": "2024-01-01 00:00:00.000",
    "endTime": "2024-01-01 23:59:59.000"
  }')

echo "   响应数据:"
echo "$RESPONSE" | jq '.' 2>/dev/null || echo "$RESPONSE"

# 提取工作流实例ID
INSTANCE_ID=$(echo "$RESPONSE" | jq -r '.id' 2>/dev/null)

if [ ! -z "$INSTANCE_ID" ] && [ "$INSTANCE_ID" != "null" ]; then
    echo ""
    echo "   ✓ 工作流启动成功"
    echo "   工作流实例ID: $INSTANCE_ID"
    echo ""

    # 等待一会儿让工作流执行
    echo "3. 等待工作流执行 (3秒)..."
    sleep 3
    echo ""

    # 查询工作流状态
    echo "4. 查询工作流执行结果..."
    RESULT=$(curl -s "$BASE_URL/database-snapshot-manual/$INSTANCE_ID")
    echo "   执行结果:"
    echo "$RESULT" | jq '.' 2>/dev/null || echo "$RESULT"
else
    echo "   ❌ 工作流启动失败"
fi

echo ""
echo "=========================================="
echo "其他可用端点:"
echo "=========================================="
echo "  • Swagger UI:  $BASE_URL/q/swagger-ui"
echo "  • 健康检查:    $BASE_URL/q/health"
echo "  • Health UI:   $BASE_URL/q/health-ui"
echo "  • 指标监控:    $BASE_URL/q/metrics"
echo ""
echo "手动测试命令:"
echo "=========================================="
echo "curl -X POST $BASE_URL/database-snapshot-manual \\"
echo "  -H 'Content-Type: application/json' \\"
echo "  -H 'Accept: application/json' \\"
echo "  -d '{"
echo "    \"tagNames\": [\"Tag001\", \"Tag002\", \"Tag003\"],"
echo "    \"count\": 100,"
echo "    \"startTime\": \"2024-01-01 00:00:00.000\","
echo "    \"endTime\": \"2024-01-01 23:59:59.000\""
echo "  }'"
echo ""
