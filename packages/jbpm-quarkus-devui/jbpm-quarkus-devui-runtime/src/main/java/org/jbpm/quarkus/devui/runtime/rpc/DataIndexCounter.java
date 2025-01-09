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
package org.jbpm.quarkus.devui.runtime.rpc;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.MultiCacheOp;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataIndexCounter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataIndexCounter.class);

    private final Vertx vertx;
    private final MultiCacheOp<String> multi;
    private final WebClient dataIndexWebClient;
    private final String path;

    private final String query;
    private final String field;

    public DataIndexCounter(String query, String graphField, String path, Vertx vertx, WebClient dataIndexWebClient) {
        if (dataIndexWebClient == null) {
            throw new IllegalArgumentException("dataIndexWebClient is null");
        }
        this.query = query;
        this.field = graphField;
        this.path = path;

        this.vertx = vertx;
        this.dataIndexWebClient = dataIndexWebClient;

        this.multi = new MultiCacheOp<>(BroadcastProcessor.create());

        refreshCount();
    }

    public void refresh() {
        vertx.setTimer(1000, id -> {
            refreshCount();
        });
    }

    public void stop() {
        multi.onComplete();
    }

    private void refreshCount() {
        LOGGER.debug("Refreshing data for query: {}", query);

        this.dataIndexWebClient.post(path + "/graphql")
                .putHeader("content-type", "application/json")
                .sendJson(new JsonObject(query))
                .map(response -> {
                    if (response.statusCode() == 200) {
                        JsonObject responseData = response.bodyAsJsonObject().getJsonObject("data");
                        return String.valueOf(responseData.getJsonArray(field).size());
                    }
                    return "0";
                })
                .onComplete(count -> this.multi.onNext(count.result()));
    }

    public Multi<String> getMulti() {
        return multi;
    }
}