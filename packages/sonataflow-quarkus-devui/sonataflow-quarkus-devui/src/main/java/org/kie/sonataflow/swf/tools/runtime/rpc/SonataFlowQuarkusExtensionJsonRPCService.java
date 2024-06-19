/*
 * Copyright 2024 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.sonataflow.swf.tools.runtime.rpc;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Supplier;

import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class SonataFlowQuarkusExtensionJsonRPCService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SonataFlowQuarkusExtensionJsonRPCService.class);

    public static final String PROCESS_INSTANCES = "ProcessInstances";

    public static final String ALL_WORKFLOWS_IDS_QUERY = "{ \"operationName\": \"getAllProcessesIds\", \"query\": \"query getAllProcessesIds{  ProcessInstances{ id } }\" }";

    private WebClient dataIndexWebClient;

    private final String dataIndexURL;
    private final Vertx vertx;

    @Inject
    public SonataFlowQuarkusExtensionJsonRPCService(@ConfigProperty(name = "kogito.data-index.url") String dataIndexURL, Vertx vertx) {
        this.dataIndexURL = dataIndexURL;
        this.vertx = vertx;
    }

    @PostConstruct
    public void init() {
        try {
            this.dataIndexWebClient = WebClient.create(vertx, buildWebClientOptions());
        } catch (Exception ex) {
            LOGGER.warn("Cannot configure dataIndexWebClient with 'kogito.data-index.url'='{}':", dataIndexURL, ex);
        }
    }

    protected WebClientOptions buildWebClientOptions() throws MalformedURLException {
        URL dataIndexURL = new URL(this.dataIndexURL);
        return new WebClientOptions()
                .setDefaultHost(dataIndexURL.getHost())
                .setDefaultPort((dataIndexURL.getPort() != -1 ? dataIndexURL.getPort() : dataIndexURL.getDefaultPort()))
                .setSsl(dataIndexURL.getProtocol().compareToIgnoreCase("https") == 0);
    }

    public Uni<String> queryWorkflowsCount() {
        return doQuery(ALL_WORKFLOWS_IDS_QUERY, PROCESS_INSTANCES);
    }

    private Uni<String> doQuery(String query, String graphModelName) {
        if(dataIndexWebClient == null) {
            return Uni.createFrom().item("-");
        }
        return Uni.createFrom().completionStage(this.dataIndexWebClient.post("/graphql")
                .putHeader("content-type", "application/json")
                .sendJson(new JsonObject(query))
                .map(response -> {
                    if(response.statusCode() == 200) {
                        JsonObject responseData = response.bodyAsJsonObject().getJsonObject("data");
                        return String.valueOf(responseData.getJsonArray(graphModelName).size());
                    }
                    return "-";
                }).toCompletionStage());
    }

    private String doQuery(Supplier<Integer> countSupplier) {
        try {
            return String.valueOf(countSupplier.get());
        } catch (Exception ex) {
            return "-";
        }
    }

}
