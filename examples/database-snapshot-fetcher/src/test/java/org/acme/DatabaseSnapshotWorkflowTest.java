/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class DatabaseSnapshotWorkflowTest {

    @Test
    public void testManualWorkflowEndpoint() {
        // 准备测试数据
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("tagNames", Arrays.asList("Tag001", "Tag002", "Tag003"));
        requestData.put("count", 100);
        requestData.put("startTime", "2024-01-01 00:00:00.000");
        requestData.put("endTime", "2024-01-01 23:59:59.000");

        // 发送POST请求到手动工作流端点
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(requestData)
        .when()
            .post("/database-snapshot-manual")
        .then()
            .statusCode(201)  // 工作流创建成功
            .body("id", notNullValue());  // 返回工作流实例ID
    }

    @Test
    public void testHealthEndpoint() {
        given()
        .when()
            .get("/q/health")
        .then()
            .statusCode(200)
            .body("status", is("UP"));
    }

    @Test
    public void testSwaggerUI() {
        given()
        .when()
            .get("/q/swagger-ui")
        .then()
            .statusCode(200);
    }
}
