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
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DataIndexCounter {
    private static final String DATA_INDEX_URL = "kogito.data-index.url";
    private static final Logger LOGGER = LoggerFactory.getLogger(DataIndexCounter.class);

    private Multi<String> multi;
    private WebClient dataIndexWebClient;

    public DataIndexCounter(String query, String graphname, BroadcastProcessor<String> stream,WebClient dataIndexWebClient, JBPMDevUIEventPublisher eventPublisher) {
        this.dataIndexWebClient = dataIndexWebClient;

        Vertx vertx = Vertx.vertx();
        this.multi = Multi.createFrom().emitter(emitter -> {
            vertx.setTimer(1000, id -> emitter.emit("Initial data emitted"));
        });

  
        vertx.setTimer(1000, id -> refreshData(stream,query,graphname)); 
    }

    private void refreshData(BroadcastProcessor<String> stream, String query, String graphname ) {
        LOGGER.info("Refreshing data for query: {}", query);

        doQuery(query, graphname).toCompletionStage()
                 .thenAccept(result -> {
            stream.onNext(result);
                 });
    }

       private Future<String> doQuery(String query, String graphModelName) {
        if(dataIndexWebClient == null) {
            LOGGER.warn("Cannot perform '{}' query, dataIndexWebClient couldn't be set. Is DataIndex correctly? Please verify '{}' value", graphModelName, DATA_INDEX_URL);
             return Future.succeededFuture("-");
         }
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