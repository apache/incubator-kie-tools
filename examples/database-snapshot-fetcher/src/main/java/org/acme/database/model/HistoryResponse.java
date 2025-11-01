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
package org.acme.database.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * 数据库历史查询响应数据模型
 */
public class HistoryResponse {

    @JsonProperty("StatusCode")
    private Integer statusCode;

    @JsonProperty("RequestID")
    private String requestId;

    @JsonProperty("Result")
    private Object result;  // 可以是List或Object

    @JsonProperty("Error")
    private String error;

    public HistoryResponse() {
    }

    public HistoryResponse(Integer statusCode, String requestId, Object result, String error) {
        this.statusCode = statusCode;
        this.requestId = requestId;
        this.result = result;
        this.error = error;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "HistoryResponse{" +
                "statusCode=" + statusCode +
                ", requestId='" + requestId + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
