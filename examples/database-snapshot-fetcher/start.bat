@echo off
REM
REM Licensed to the Apache Software Foundation (ASF) under one
REM or more contributor license agreements.  See the NOTICE file
REM distributed with this work for additional information
REM regarding copyright ownership.  The ASF licenses this file
REM to you under the Apache License, Version 2.0 (the
REM "License"); you may not use this file except in compliance
REM with the License.  You may obtain a copy of the License at
REM
REM   http://www.apache.org/licenses/LICENSE-2.0
REM
REM Unless required by applicable law or agreed to in writing,
REM software distributed under the License is distributed on an
REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
REM KIND, either express or implied.  See the License for the
REM specific language governing permissions and limitations
REM under the License.
REM

echo ==========================================
echo 数据库快照定时获取系统 - 快速启动脚本
echo ==========================================
echo.

REM 检查环境变量
if "%DATABASE_API_SERVER_IP%"=="" (
    echo ⚠️  警告: DATABASE_API_SERVER_IP 环境变量未设置
    echo    将使用默认值: localhost
    set DATABASE_API_SERVER_IP=localhost
) else (
    echo ✓ 数据库API地址: %DATABASE_API_SERVER_IP%
)

if "%DATABASE_API_SERVER_PORT%"=="" (
    echo ⚠️  警告: DATABASE_API_SERVER_PORT 环境变量未设置
    echo    将使用默认值: 5000
    set DATABASE_API_SERVER_PORT=5000
) else (
    echo ✓ 数据库API端口: %DATABASE_API_SERVER_PORT%
)

echo.
echo ==========================================
echo 正在启动应用...
echo ==========================================
echo.

REM 启动Quarkus开发模式
mvn quarkus:dev

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ 启动失败！
    echo.
    echo 请检查:
    echo   1. Maven是否已安装 (mvn --version^)
    echo   2. Java 17+是否已安装 (java -version^)
    echo   3. 是否在正确的项目目录下
    echo.
    pause
    exit /b 1
)
