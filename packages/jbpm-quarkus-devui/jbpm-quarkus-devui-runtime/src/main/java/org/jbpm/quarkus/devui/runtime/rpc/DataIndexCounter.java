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
import io.smallrye.mutiny.subscription.MultiEmitter;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataIndexCounter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataIndexCounter.class);
    private String query;
    private String field;

    private Vertx vertx;
    private Multi<String> multi;
    private WebClient dataIndexWebClient;
    private String count = "-";
    private MultiEmitter<? super String> emitter;
    private long vertxTimer;

    public DataIndexCounter(String query, String graphField, WebClient dataIndexWebClient) {
        if(dataIndexWebClient == null) {
            throw new IllegalArgumentException("dataIndexWebClient is null");
        }
        this.query = query;
        this.field = graphField;
        this.dataIndexWebClient = dataIndexWebClient;
        this.vertx = Vertx.vertx();

        this.multi = Multi.createFrom().emitter(emitter -> {
            this.emitter = emitter;
            vertxTimer = vertx.setPeriodic(3000, id -> {
                this.emit();
            });
            this.emit();
        });
        refreshCount();
        }

        public void refresh() {
            vertx.setTimer(3000, id -> {
                refreshCount();
            });
        }
        public void stop() {
            vertx.cancelTimer(vertxTimer);
        }
    
        private void emit() {
            emitter.emit(count);
        }

        private void refreshCount() {
        LOGGER.info("Refreshing data for query: {}", query);

        doQuery(query, field).toCompletionStage()
                 .thenAccept(result -> {
                    this.count = result;
                    this.emit();
                 });
    }

       private Future<String> doQuery(String query, String graphModelName) {
         return this.dataIndexWebClient.post("/graphql")
                 .putHeader("content-type", "application/json")
                 .sendJson(new JsonObject(query))
                 .map(response -> {
                    if(response.statusCode() == 200) {
                         JsonObject responseData = response.bodyAsJsonObject().getJsonObject("data");
                         return String.valueOf(responseData.getJsonArray(graphModelName).size());
                     }
                     return "-";
                 });
     }

    public Multi<String> getMulti() {
        return multi;
    }
}