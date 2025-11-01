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

echo "=========================================="
echo "数据库快照定时获取系统 - 快速启动脚本"
echo "=========================================="
echo ""

# 检查环境变量
if [ -z "$DATABASE_API_SERVER_IP" ]; then
    echo "⚠️  警告: DATABASE_API_SERVER_IP 环境变量未设置"
    echo "   将使用默认值: localhost"
    export DATABASE_API_SERVER_IP=localhost
else
    echo "✓ 数据库API地址: $DATABASE_API_SERVER_IP"
fi

if [ -z "$DATABASE_API_SERVER_PORT" ]; then
    echo "⚠️  警告: DATABASE_API_SERVER_PORT 环境变量未设置"
    echo "   将使用默认值: 5000"
    export DATABASE_API_SERVER_PORT=5000
else
    echo "✓ 数据库API端口: $DATABASE_API_SERVER_PORT"
fi

echo ""
echo "=========================================="
echo "正在启动应用..."
echo "=========================================="
echo ""

# 启动Quarkus开发模式
mvn quarkus:dev

# 如果Maven命令失败
if [ $? -ne 0 ]; then
    echo ""
    echo "❌ 启动失败！"
    echo ""
    echo "请检查:"
    echo "  1. Maven是否已安装 (mvn --version)"
    echo "  2. Java 17+是否已安装 (java -version)"
    echo "  3. 是否在正确的项目目录下"
    echo ""
    exit 1
fi
